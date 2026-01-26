# HZU-Jiangxia-Android2026
目标：从零基础到完成一个完整Android应用，考核学习能力和积极态度
- [x] 前置知识：Java基础

**建议**
1. 一定要动手敲代码，每天至少投入3-4小时，保持学习连贯性
2. 遇到问题先搜索，再整理清晰的问题描述求助
3. 记录学习心得和技术难点
4. 思考如何让你的应用更好用

## 第一阶段：基础入门（建议完成时间：5天）

### 学习任务：

1. 工作环境搭建
	- 安装Android Studio（android-studio-2021-2-1-15.exe）
	- 配置JDK和SDK
	- 创建第一个Hello World项目并在模拟器、真机上运行（截图）

2. 加入Git项目（Fork），将项目克隆（Clone）到自己帐号下
	> 学习资源: [Git教程](https://liaoxuefeng.com/books/git/introduction/index.html)

	- [仓库地址](https://github.com/xiaowen-UniversityLab/HZU-Jiangxia-Android2026)
	- 在 个人仓库/ 目录下，按"学号-姓名"创建工作目录
	- 学习作业的上传示例

3. Android基础概念
	> 学习资源：[Android编程权威指南_Java版](https://pan.baidu.com/s/1KNAVmCYCyxkdUS-K3vJRTQ?pwd=7m5x) 提取码7m5x

	- 学习Activity生命周期
	- 掌握基本UI组件（TextView、Button、EditText等）
	- 理解布局（LinearLayout、Relativelayout、ConstraintLayout）
	- 学习Intent和页面跳转
	- 四大组件

4. 第一阶段作业
	- 制作一个“个人简介”应用：
		1. 包含两个Activity
		2. 个人信息展示页面
		3. 跳转到联系方式页面
	- 按照示例格式，上传到个人仓库，最后提交pull request，内容包含：
		1. 学习笔记（使用 Markdown 编写。内容：学习过程中遇到问题的思考）
		2. 相关代码
		3. 相关截图（环境搭建成功截图、模拟器&真机运行截图、“个人简介”运行截图）

-------
## 第二阶段：核心技能（建议完成时间：7天）

### 学习任务：

1. 数据存储
	- SharedPreferences
	- 本地文件存储
	- Room数据库CURD操作

2. 网络请求
	- 理解HTTP基础
	- 学习使用Retrofit
	- 解析JSON数据

3. 异步处理
	- Handler与Message
	- AsyncTask基础
	- 了解线程安全概念

4. 第二阶段作业
	- 实现GET请求，接口：[https://dog.ceo/api/breeds/image/random](https://dog.ceo/api/breeds/image/random)，使用ImageView展示图片
	- 实现一个POST接口请求，不限制数据展示方式，使用数据库保存前20条数据，在无网络时加载数据库数据
	- 注意代码规范（命名、注释）
	- git上传代码（保持示例格式）
	- git上传学习笔记（md格式、问题、思路、解决方法）

-------
## 第三阶段：综合应用（建议完成时间：15天）

### 学习任务：

1. 高级UI
	- RecyclerView和Adapter
	- 自定义View基础
	- Dialog弹窗、自定义样式

2. 架构模式
	- MVC、MVVM模式理解
	- 学习简单的项目结构设计

3. 调试与优化
	- Logcat、断点使用技巧
	- 内存泄漏基础概念

4. 第三阶段作业
	- 自选主题的完整应用开发
		1. 至少3个Activity/Fragment
		2. 本地数据持久化
		3. 网络数据获取
		4. 列表展示（RecyclerView）
	- 加分项：
		1. 使用第三方库（如Glide图片加载）
		2. 涉及四大组件
		3. 自定义View
		4. 动画效果
	- git上传代码&学习笔记，附上截图更好


注意：本计划设计具有一定挑战性，能够自然区分出学习态度积极、自我驱动力强的学生。完成全部任务的学生，无论基础如何，都展现了优秀的学习能力和积极态度。
**祝学习顺利，期待看到你们的作品！**