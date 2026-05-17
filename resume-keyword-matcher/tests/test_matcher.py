"""matcher.py 单元测试"""

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from matcher import tokenize, extract_keywords, compute_match


class TestTokenize(unittest.TestCase):
    def test_english_words(self):
        tokens = tokenize("I love Python and Java")
        self.assertIn("python", tokens)
        self.assertIn("java", tokens)
        self.assertNotIn("i", tokens)
        self.assertNotIn("and", tokens)

    def test_chinese_bigrams(self):
        tokens = tokenize("熟悉微服务架构")
        self.assertIn("微服务", tokens)
        self.assertIn("服务", tokens)
        self.assertIn("架构", tokens)

    def test_mixed_text(self):
        tokens = tokenize("掌握Python和数据结构")
        self.assertIn("python", tokens)
        self.assertIn("数据结构", tokens)


class TestExtractKeywords(unittest.TestCase):
    def test_programming_language(self):
        keywords = extract_keywords("I know Python, Java and C++")
        self.assertIn("python", keywords)
        self.assertIn("java", keywords)
        self.assertIn("c++", keywords)

    def test_chinese_skills(self):
        keywords = extract_keywords("熟练掌握微服务架构和Docker容器化部署")
        self.assertIn("微服务", keywords)
        self.assertIn("docker", keywords)
        self.assertIn("容器化", keywords)


class TestComputeMatch(unittest.TestCase):
    def setUp(self):
        self.resume = """
        熟练掌握Python和Java编程语言，熟悉MySQL数据库和Redis缓存。
        有Linux环境下开发和Git版本控制经验。具备良好的团队协作能力。
        """
        self.job = """
        岗位要求：
        1. 精通Java或Python
        2. 熟悉MySQL、Redis
        3. 掌握Linux常用命令
        4. 了解Docker容器化
        5. 有微服务架构经验优先
        6. 良好的团队协作和沟通能力
        """

    def test_basic_match(self):
        result = compute_match(self.resume, self.job)
        self.assertGreater(result["total"], 0)
        self.assertGreater(result["matched_count"], 0)
        self.assertIn("python", result["matched_keywords"])
        self.assertIn("java", result["matched_keywords"])
        self.assertIn("mysql", result["matched_keywords"])
        self.assertIn("redis", result["matched_keywords"])

    def test_missed_keywords(self):
        result = compute_match(self.resume, self.job)
        self.assertIn("docker", result["missed_keywords"])
        self.assertIn("微服务", result["missed_keywords"])

    def test_match_rate(self):
        result = compute_match(self.resume, self.job)
        self.assertIsInstance(result["match_rate"], float)
        self.assertGreaterEqual(result["match_rate"], 0)
        self.assertLessEqual(result["match_rate"], 100)


if __name__ == "__main__":
    unittest.main()
