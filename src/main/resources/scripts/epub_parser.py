import epub, os
from qcloud_cos import CosConfig, CosS3Client

def parse_epub(bookName, epubPath, fileName):
    book = epub.read_epub(epubPath)

    # 获取元数据
    title = book.get_metadata('DC', 'title')[0][0].replace("/", "_")
    author = book.get_metadata('DC', 'creator')[0][0]

    # 初始化COS客户端
    cos_config = CosConfig(
        Region=os.getenv('COS_REGION'),
        SecretId=os.getenv('COS_SECRET_ID'),
        SecretKey=os.getenv('COS_SECRET_KEY')
    )
    cos_client = CosS3Client(cos_config)

    # 上传原始文件
    original_key = f"books/{title}/original/{title}.epub"
    cos_client.upload_file(
        Bucket=os.getenv('COS_BUCKET'),
        LocalFilePath=epubPath,
        Key=original_key
    )

    # 处理章节
    for i, item in enumerate(book.get_items_of_type(epub.EpubHtml)):
        chapter_key = f"books/{title}/content/chapter_{i+1}.txt"
        cos_client.put_object(
            Bucket=os.getenv('COS_BUCKET'),
            Key=chapter_key,
            Body=item.get_content().decode('utf-8')
        )

    # 提取封面（如果有）
    cover = next((item for item in book.get_items_of_type(epub.EpubImage)), None)
    if cover:
        cover_key = f"books/{title}/cover/{title}-cover.jpg"
        cos_client.put_object(
            Bucket=os.getenv('COS_BUCKET'),
            Key=cover_key,
            Body=cover.get_content()
        )

    return {
        "title": title,
        "original_path": original_key,
        "cover_path": cover_key if cover else None
    }