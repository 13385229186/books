import sys
import json
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity

def main():
    # 1. 读取输入参数
    args = json.loads(sys.argv[1])
    user_id = int(args['user_id'])
    behavior_data = json.loads(args['behavior_data'])

    # 2. 执行推荐算法
    recommendations = generate_recommendations(user_id, behavior_data)

    # 3. 输出结果
    print(json.dumps(recommendations))

def generate_recommendations(target_user_id, behavior_data):
    """基于用户行为的协同过滤推荐"""
    try:
        # 转换为DataFrame
        df = pd.DataFrame(behavior_data)

        # 创建用户-物品矩阵
        user_item_matrix = df.pivot_table(
            index='user_id',
            columns='book_id',
            values='weight',
            fill_value=0
        )

        # 计算用户相似度
        user_similarity = cosine_similarity(user_item_matrix)
        user_sim_df = pd.DataFrame(
            user_similarity,
            index=user_item_matrix.index,
            columns=user_item_matrix.index
        )

        # 找到最相似的5个用户（排除自己）
        similar_users = user_sim_df[target_user_id].sort_values(ascending=False)[1:6].index

        # 获取相似用户喜欢的书籍
        similar_users_books = df[df['user_id'].isin(similar_users)]

        # 计算推荐分数（加权平均）
        recommendations = similar_users_books.groupby('book_id')['weight'].mean()

        # 过滤目标用户已经看过的书籍
        target_user_books = set(df[df['user_id'] == target_user_id]['book_id'])
        recommendations = recommendations[~recommendations.index.isin(target_user_books)]

        # 转换为字典并返回Top 20
        return recommendations.sort_values(ascending=False).head(20).to_dict()

    except Exception as e:
        print(f"Error: {str(e)}", file=sys.stderr)
        return {}

if __name__ == "__main__":
    main()