import os, argparse, warnings, sys, io, re

from qcloud_cos import CosConfig, CosS3Client
from dotenv import load_dotenv
from pathlib import Path

def avatar_upload(filePath, fileName):
    # 初始化COS客户端
    cos_config = CosConfig(
        Region=os.getenv('COS_REGION'),
        SecretId=os.getenv('COS_SECRET_ID'),
        SecretKey=os.getenv('COS_SECRET_KEY')
    )
    cos_client = CosS3Client(cos_config)

    # 上传头像
    avatar_key = f"avatars/{fileName}"
    cos_client.upload_file(
        Bucket=os.getenv('COS_BUCKET'),
        LocalFilePath=filePath,
        Key=avatar_key,
    )

    return {
        "avatar_path": avatar_key
    }

# 主函数
def main():
    # 参数解析
    parser = argparse.ArgumentParser(description='头像上传工具')
    parser.add_argument('--filePath', required=True, help='头像本地存储位置')
    parser.add_argument('--fileName', required=True, help='头像文件名称')
    args = parser.parse_args()

    # 调用核心逻辑
    result = avatar_upload(
        filePath=args.filePath,
        fileName=args.fileName,
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