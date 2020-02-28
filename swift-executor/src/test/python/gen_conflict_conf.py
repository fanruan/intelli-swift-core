import json
res = dict()
table_names = []
task_type_limits = {"COLLATE": 1, "TREASURE_IMPORT": 1, "TREASURE_ANALYSIS": 1, "REALTIME": 1, "RECOVERY": 1,
                    "TRANSFER": 1, "INDEX": 1, "DELETE": 1, "TRUNCATE": 1, "UPLOAD": 1, "DOWNLOAD": 1, "HISTORY": 1,
                    "QUERY": 1}
res["conflicts"] = []
for table in table_names:
    obj = {
            "sourceKey": table,
            "executorTaskType": None,
            "lockKey": None,
            "semaphore": -1
          }
    res["conflicts"].append(obj)
for task_type in task_type_limits.keys():
    obj = {
            "sourceKey": None,
            "executorTaskType": task_type,
            "lockKey": None,
            "semaphore": task_type_limits[task_type]
          }
    res["conflicts"].append(obj)

res["stopTheWorldTaskTypes"] = ["TREASURE_ANALYSIS"]
# 注意当前路径不是 resources 文件夹的路径，需要移动
with open("./conflict-conf.json", "w") as f:
    json.dump(res, f)