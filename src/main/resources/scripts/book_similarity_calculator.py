import sys, argparse
import json
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity

def calculate_similarity(behavior_data):
    """输入: 全量用户行为数据 | 输出: 书籍相似度字典"""
    try:
        # 构建物品-用户矩阵
        df = pd.DataFrame(behavior_data)
        item_user_matrix = df.pivot_table(
            index='book_id',
            columns='user_id',
            values='weight',
            fill_value=0
        )

        # 计算余弦相似度
        sim_matrix = cosine_similarity(item_user_matrix)
        # print("sim_matrix:::" + sim_matrix)

        # 转换为嵌套字典格式
        return {
            int(book_id): {
                int(other_id): float(score)
                for other_id, score in zip(item_user_matrix.index, sim_matrix[i])
                if score > 0.1 and book_id != other_id  # 过滤低相似度和自身
            }
            for i, book_id in enumerate(item_user_matrix.index)
        }
    except Exception as e:
        print(f"Error: {str(e)}", file=sys.stderr)
        return {}

if __name__ == "__main__":
    # 参数解析
    parser = argparse.ArgumentParser(description='书籍相似度计算工具')
    parser.add_argument('--behavior_data', required=True, help='全量用户行为数据')

    # 计算并输出结果
    result = calculate_similarity(json.loads(parser.parse_args().behavior_data))
    print(json.dumps(result))