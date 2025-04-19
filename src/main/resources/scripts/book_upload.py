import json
import os, argparse, warnings, sys, io

from qcloud_cos import CosConfig, CosS3Client, CosServiceError
from dotenv import load_dotenv
from pathlib import Path
import logging

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='\n%(asctime)s - %(levelname)s - %(message)s\n',
    encoding='utf-8'
)
logger = logging.getLogger(__name__)

def validate_file(file_path: str, allowed_extensions: list = None) -> bool:
    """验证文件是否存在且符合要求"""
    if not os.path.exists(file_path):
        logger.error(f"文件不存在: {file_path}")
        return False

    if allowed_extensions:
        ext = os.path.splitext(file_path)[1].lower()
        if ext not in allowed_extensions:
            logger.error(f"不支持的文件类型: {ext}. 只支持: {', '.join(allowed_extensions)}")
            return False

    return True

def oss_upload(cos_client: CosS3Client, oss_key: str, local_path: str, overwrite: bool = False) -> bool:
    """上传文件到COS"""
    try:
        # 检查文件是否已存在
        if not overwrite:
            try:
                cos_client.head_object(
                    Bucket=os.getenv('COS_BUCKET'),
                    Key=oss_key
                )
                logger.warning(f"文件已存在: {oss_key}. 使用 --overwrite 覆盖")
                return False
            except CosServiceError as e:
                if e.get_status_code() != 404:
                    raise

        # 执行上传
        cos_client.upload_file(
            Bucket=os.getenv('COS_BUCKET'),
            LocalFilePath=local_path,
            Key=oss_key
        )
        logger.info(f"成功上传: {local_path} -> {oss_key}")
        return True
    except Exception as e:
        logger.error(f"上传失败: {local_path} -> {oss_key}. 错误: {str(e)}")
        return False

# 主函数
def main():
    # 参数解析
    parser = argparse.ArgumentParser(description='电子书及封面图片上传OSS工具')
    parser.add_argument('--bookName', required=True, help='书籍名称')
    parser.add_argument('--filePath', required=False, help='电子书文件路径')
    parser.add_argument('--coverPath', required=False, help='封面图片路径')
    parser.add_argument('--overwrite', type=lambda x: x.lower() == 'true', default=False)
    args = parser.parse_args()
    book_name = args.bookName

    # 验证参数
    if not args.filePath and not args.coverPath:
        logger.error("必须提供至少一个文件路径(--filePath或--coverPath)")
        sys.exit(1)

    # 验证文件
    if args.filePath and not validate_file(args.filePath, ['.pdf']):
        sys.exit(1)
    if args.coverPath and not validate_file(args.coverPath, ['.jpg', '.jpeg', '.png']):
        sys.exit(1)

    # 初始化COS客户端
    try:
        cos_config = CosConfig(
            Region=os.getenv('COS_REGION'),
            SecretId=os.getenv('COS_SECRET_ID'),
            SecretKey=os.getenv('COS_SECRET_KEY')
        )
        cos_client = CosS3Client(cos_config)
    except Exception as e:
        logger.error(f"COS客户端初始化失败: {str(e)}")
        sys.exit(1)

    # 上传文件
    original_key = None
    if args.filePath:
        ext = os.path.splitext(args.filePath)[1].lower()
        original_key = f"books/{args.bookName}/ebook/{args.bookName}{ext}"
        if not oss_upload(cos_client, original_key, args.filePath, args.overwrite):
            sys.exit(1)

    # 上传封面
    cover_key = None
    if args.coverPath:
        ext = os.path.splitext(args.coverPath)[1].lower()
        cover_key = f"books/{args.bookName}/cover/{args.bookName}-cover{ext}"
        if not oss_upload(cos_client, cover_key, args.coverPath, args.overwrite):
            sys.exit(1)

    result = {
        "bookName": book_name,
        "original_path": original_key,
        "cover_path": cover_key
    }
    print(json.dumps(result, ensure_ascii=False))  # 输出标准 JSON

# 执行入口
if __name__ == '__main__':
    logger.info("进入python脚本")
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

    # 忽略警告
    warnings.filterwarnings("ignore", category=UserWarning)
    warnings.filterwarnings("ignore", category=FutureWarning)

    env_path = Path(__file__).resolve().parent.parent.parent.parent / '.env' # 定位.env文件路径
    # 加载环境变量
    if not load_dotenv(dotenv_path=env_path):
        logger.warning("未找到.env文件或加载失败")
    main()