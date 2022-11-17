# NewsRecommendSystem
本系统架构为springboot+mysql+webmagic+selenium+redis+部署spark环境的linux(推荐centos系统)的服务器
主要分为两部分
第一部分:scala部分用于模型训练以及数据预处理,包依赖管理使用sbt,冲突管理代码全部在内
第二部分:springboot使用maven包依赖管理,需要自己配置端口,数据库redis和邮箱验证秘钥等

对于非关系型数据没有使用mongdb直接使用字符串拼接存储在mysql中,本项目并未使用sparkstreaming,kafuka,hbase,flink等技术,毕竟需要分布式环境和hadoop基础才能运行,对大部分人并不合适

训练集部分使用的是movie-100数据,如果需要训练集可以评论,gitlib限制项目不能超过100m,传不上去

scala部分已经上传训练好的model,需要可以直接部署,但是相似度矩阵计算用i7-97000尝试了三天,一天12个小时,每次都遇到各种各样的问题,最后遇到gc丢失最先gdbc对象,就不想再等了,毕竟spark为集群准备的,
单机性能再高也无济于事

视频教程在b站: https://www.bilibili.com/video/BV1eq4y1776f?from=search&seid=13616556324097728779
文字资料在csdn:https://blog.csdn.net/weixin_49139876/article/details/116724661
数据库:mysql
链接：https://pan.baidu.com/s/1jLzfYbpsfHEjS4S17IyQnQ 
提取码：1234 

原创不易,一键三联哦
