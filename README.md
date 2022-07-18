# Login 登录Mod
这是一个为Minecraft Fabric打造的登录模组，可以进行简单的账户验证，没有数据加密措施。

## 指令
### 基础指令（不需要权限）
- 注册：/register <密码>
- 登录：/login <密码>
- 更改自己的密码：/password <旧密码> <新密码>  

### 管理员指令（需要OP权限）
- 查询玩家密码：/password query <玩家名 或 玩家选择器>  
  只能选中一个玩家  
- 强制更改指定玩家的密码：/password <玩家名 或 玩家选择器> <新密码>  
  只能选中一个玩家，管理员可以更改其他管理员的密码！  
- 将当前所有账户数据存储到文件：/password dump  
  存储目录为".../.minecraft/config/login.properties"或".../.minecraft/config/versions/<VersionName>/login.properties"
- 从文件读取当前所有账户数据：/password load  
  目标文件目录见上

## 使用注意事项
1. 账户数据存储位置在 ".../.minecraft/config/login.properties" 或 ".../.minecraft/config/versions/<VersionName>/login.properties"中；  
  正在运行的服务端的账户数据在每次关闭服务器时自动进行存储，读取也会在开启服务器时进行；  
  存储的数据不会进行任何加密！请自行注意保密措施。  
2. 目前仅支持中文的聊天版提示。  
