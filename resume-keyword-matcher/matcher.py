#!/usr/bin/env python3
"""
简历关键词匹配分析工具

分析简历文本与目标岗位描述之间的关键词匹配程度，
输出匹配率、匹配关键词和缺失关键词建议。
"""

import re
import sys
import argparse
from pathlib import Path


# ---------- 中文停用词 ----------
_STOP_WORDS_CN = {
    "的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都",
    "一", "一个", "上", "也", "很", "到", "说", "要", "去", "你",
    "会", "着", "没有", "看", "好", "自己", "这", "他", "她", "它",
    "们", "那", "些", "为", "能", "与", "而", "但", "或", "被",
    "从", "把", "对", "以", "及", "之", "等", "让", "将", "其",
    "所", "中", "还", "可", "已", "于", "更", "做", "用", "过",
    "使", "因", "如", "向", "并", "所", "该", "此", "每", "各",
    "哪", "谁", "什么", "怎么", "为何", "如何", "多少",
    "非常", "比较", "特别", "主要", "一定", "必须", "可以", "需要",
    "已经", "正在", "通过", "进行", "包括", "具有", "具备",
    "相关", "以上", "以下", "方面", "方式", "程度", "内容", "部分",
}

# ---------- 英文停用词 ----------
_STOP_WORDS_EN = {
    "a", "an", "the", "and", "or", "but", "in", "on", "at", "to",
    "for", "of", "with", "by", "from", "as", "is", "are", "was",
    "were", "be", "been", "being", "have", "has", "had", "do", "does",
    "did", "will", "would", "could", "should", "may", "might", "shall",
    "can", "need", "dare", "must", "ought", "used", "this", "that",
    "these", "those", "it", "its", "they", "them", "their", "we", "our",
    "you", "your", "he", "she", "his", "her", "i", "me", "my", "who",
    "whom", "which", "what", "how", "when", "where", "why", "not", "no",
    "nor", "none", "neither", "so", "such", "only", "just", "very",
    "too", "much", "more", "most", "less", "least", "few", "little",
    "enough", "all", "each", "every", "both", "either", "neither", "some",
    "any", "many", "several", "about", "above", "after", "again",
    "against", "before", "between", "below", "down", "during", "except",
    "off", "out", "over", "up", "under", "upon", "than", "then", "once",
}

# ---------- 内置技能词典 ----------
_BUILTIN_KEYWORDS = {
    # 编程语言
    "python", "java", "javascript", "typescript", "go", "rust", "c++", "c#",
    "c", "swift", "kotlin", "scala", "ruby", "php", "perl", "lua", "dart",
    "sql", "html", "css", "sass", "less", "shell", "bash", "powershell",
    # 框架 & 库
    "spring", "springboot", "spring cloud", "mybatis", "hibernate",
    "django", "flask", "fastapi", "tornado", "express", "next.js", "nuxt",
    "react", "vue", "angular", "jquery", "bootstrap", "tailwind",
    "tensorflow", "pytorch", "keras", "scikit-learn", "pandas", "numpy",
    "hadoop", "spark", "flink", "kafka", "rabbitmq",
    # 数据库
    "mysql", "postgresql", "oracle", "redis", "mongodb", "elasticsearch",
    "sqlite", "mariadb", "clickhouse", "tdengine",
    # 工具 & 平台
    "git", "docker", "kubernetes", "k8s", "jenkins", "nginx", "linux",
    "maven", "gradle", "npm", "yarn", "webpack", "vite", "ci/cd",
    # 云 & 架构
    "aws", "azure", "阿里云", "腾讯云", "华为云", "微服务", "分布式",
    "容器化", "云原生", "devops", "serverless", "高可用", "负载均衡",
    # 软技能
    "团队协作", "沟通能力", "项目管理", "需求分析", "文档编写",
    "problem solving", "teamwork", "leadership", "communication",
    # AI / 数据
    "机器学习", "深度学习", "自然语言处理", "计算机视觉", "数据分析",
    "数据挖掘", "大数据", "推荐系统", "aigc", "llm", "rag",
    "machine learning", "deep learning", "nlp", "data analysis",
    # 其他
    "数据结构", "算法", "操作系统", "计算机网络", "软件工程", "设计模式",
}


def read_text(source: str) -> str:
    """从文件或直接字符串读取文本。"""
    if Path(source).exists():
        return Path(source).read_text(encoding="utf-8")
    return source


def tokenize(text: str) -> set:
    """
    简单分词：提取英文单词 + 中文词组。
    - 英文单词按空格/标点分割，转小写
    - 中文按 2~4 字词组滑动窗口提取
    """
    text = text.lower()
    tokens = set()

    # 英文单词
    en_words = re.findall(r"[a-z][a-z0-9+#/.-]+", text)
    for w in en_words:
        w = w.strip(".-/+")
        if w and w not in _STOP_WORDS_EN and len(w) > 1:
            tokens.add(w)

    # 中文：提取连续汉字并滑动窗口
    chinese_chunks = re.findall(r"[\u4e00-\u9fff]+", text)
    for chunk in chinese_chunks:
        if chunk in _STOP_WORDS_CN:
            continue
        # 2 字词
        for i in range(len(chunk) - 1):
            bigram = chunk[i : i + 2]
            if bigram not in _STOP_WORDS_CN:
                tokens.add(bigram)
        # 3 字词
        for i in range(len(chunk) - 2):
            trigram = chunk[i : i + 3]
            if trigram not in _STOP_WORDS_CN:
                tokens.add(trigram)
        # 4 字词
        for i in range(len(chunk) - 3):
            tetragram = chunk[i : i + 4]
            if tetragram not in _STOP_WORDS_CN:
                tokens.add(tetragram)

    return tokens


def extract_keywords(text: str) -> set:
    """从文本中提取命中内置技能词典的关键词。"""
    tokens = tokenize(text)
    keywords = set()

    for token in tokens:
        if token in _BUILTIN_KEYWORDS:
            keywords.add(token)

    # 从显式分隔列表中提取
    explicit_lists = re.split(r"[,，;；、]+", text)
    for item in explicit_lists:
        item = item.strip().lower()
        item = re.sub(r"^[\s\d.\-*•·]+|[\s\d.\-*•·]+$", "", item)
        if item in _BUILTIN_KEYWORDS:
            keywords.add(item)

    return keywords


def compute_match(resume_text: str, job_text: str) -> dict:
    """计算匹配度，返回完整报告。"""
    job_keywords = extract_keywords(job_text)
    resume_keywords = extract_keywords(resume_text)

    matched = job_keywords & resume_keywords
    missed = job_keywords - resume_keywords

    total = len(job_keywords)
    match_count = len(matched)
    rate = round(match_count / total * 100, 1) if total > 0 else 0.0

    return {
        "total": total,
        "matched_count": match_count,
        "matched_keywords": sorted(matched),
        "missed_keywords": sorted(missed),
        "match_rate": rate,
        "suggestions": sorted(missed)[:10],
    }


def print_report(result: dict):
    """格式化打印匹配报告。"""
    border = "═" * 46
    print()
    print(f"╔{border}╗")
    print(f"║{'简历关键词匹配分析报告':^44}║")
    print(f"╚{border}╝")
    print()
    print(f"  岗位关键词总数: {result['total']}")
    print(f"  简历匹配关键词: {result['matched_count']}")
    print(f"  匹配率: {result['match_rate']}%")
    print()

    if result["matched_keywords"]:
        print("  ✅ 已匹配关键词:")
        items = result["matched_keywords"]
        for i in range(0, len(items), 5):
            row = items[i : i + 5]
            print(f"     {', '.join(row)}")
        print()

    if result["missed_keywords"]:
        print("  ❌ 缺失关键词:")
        items = result["missed_keywords"]
        for i in range(0, len(items), 5):
            row = items[i : i + 5]
            print(f"     {', '.join(row)}")
        print()

    if result["suggestions"]:
        print("  💡 建议补充方向:")
        for s in result["suggestions"][:5]:
            print(f"     - {s}")
        print()

    rate = result["match_rate"]
    if rate >= 80:
        tag = "A+ · 高度匹配，可以直接投"
    elif rate >= 60:
        tag = "B+ · 基本匹配，局部优化后可投"
    elif rate >= 40:
        tag = "C+ · 部分匹配，建议补充缺失技能"
    else:
        tag = "D · 匹配度低，需要大幅调整简历方向"
    print(f"  📊 综合评级: {tag}")
    print()


def main():
    parser = argparse.ArgumentParser(
        description="简历关键词匹配分析工具",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""使用示例:
  python matcher.py -r resume.txt -j job.txt
  python matcher.py   # 交互模式""",
    )
    parser.add_argument("-r", "--resume", help="简历文件路径")
    parser.add_argument("-j", "--job", help="岗位描述文件路径")
    args = parser.parse_args()

    if args.resume and args.job:
        resume_text = read_text(args.resume)
        job_text = read_text(args.job)
    else:
        print("📝 请粘贴你的简历文本（输入完成后按 Ctrl+D / Ctrl+Z 结束）：")
        resume_text = sys.stdin.read().strip()
        print("\n📋 请粘贴岗位描述文本（输入完成后按 Ctrl+D / Ctrl+Z 结束）：")
        job_text = sys.stdin.read().strip()

    if not resume_text or not job_text:
        print("❌ 错误：简历和岗位描述都不能为空。")
        sys.exit(1)

    result = compute_match(resume_text, job_text)
    print_report(result)


if __name__ == "__main__":
    main()
