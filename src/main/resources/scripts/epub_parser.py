import os, argparse, warnings, sys, io, re

import ebooklib
from ebooklib import epub
from qcloud_cos import CosConfig, CosS3Client
from charset_normalizer import detect
from dotenv import load_dotenv
from pathlib import Path

def parse_epub_image(bookName, filePath, fileName, coverPath):
    book = epub.read_epub(filePath)

    # 初始化COS客户端
    cos_config = CosConfig(
        Region=os.getenv('COS_REGION'),
        SecretId=os.getenv('COS_SECRET_ID'),
        SecretKey=os.getenv('COS_SECRET_KEY')
    )
    cos_client = CosS3Client(cos_config)

    # 上传原始文件
    original_key = f"books/{bookName}/original/{fileName}"
    cos_client.upload_file(
        Bucket=os.getenv('COS_BUCKET'),
        LocalFilePath=filePath,
        Key=original_key
    )

    # 上传封面
    cover_key = f"books/{bookName}/cover/{bookName}-cover.jpg"
    cos_client.upload_file(
        Bucket=os.getenv('COS_BUCKET'),
        LocalFilePath=coverPath,
        Key=cover_key,
    )

    # 处理章节
    for i, item in enumerate(book.get_items_of_type(ebooklib.ITEM_DOCUMENT)):
        # 获取章节标题（如果没有则使用默认值）
        chapter_title = item.get_name() or f"chapter_{i+1}"
        chapter_key = f"books/{bookName}/content/chapter_{i+1}.txt"
        # 使用正则获取body中的内容
        pattern = r'<body>(.*?)</body>'
        match = re.search(pattern, safe_decode(item.get_content()), re.DOTALL)

        cos_client.put_object(
            Bucket=os.getenv('COS_BUCKET'),
            Key=chapter_key,
            Body=match.group(1),
            Metadata={
                "original-title": chapter_title,  # 保留原始标题元数据
                "chapter-num": str(i+1)          # 保留章节序号
            }
        )

    return {
        "bookName": bookName,
        "original_path": original_key,
        "cover_path": cover_key
    }

def safe_decode(content):
    result = detect(content)
    return content.decode(result['encoding'] if result['confidence'] > 0.8 else 'utf-8')

# 主函数
def main():
    # 参数解析
    parser = argparse.ArgumentParser(description='EPUB电子书及封面图片解析工具')
    parser.add_argument('--bookName', required=True, help='书籍名称')
    parser.add_argument('--filePath', required=True, help='EPUB文件路径')
    parser.add_argument('--fileName', required=True, help='原始EPUB文件名称')
    parser.add_argument('--coverPath', required=True, help='封面图片路径')
    args = parser.parse_args()

    # 调用核心逻辑
    result = parse_epub_image(
        bookName=args.bookName,
        filePath=args.filePath,
        fileName=args.fileName,
        coverPath=args.coverPath
    )
    print(result)

# 执行入口
if __name__ == '__main__':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
    env_path = Path(__file__).resolve().parent.parent.parent.parent / '.env' # 定位.env文件路径
    warnings.filterwarnings("ignore", category=UserWarning)
    warnings.filterwarnings("ignore", category=FutureWarning)
    load_dotenv(dotenv_path=env_path)
    main()