# 开发指南

## 待办

* TODO: 下载时如何配置settings.xml, 以使其他项目可到指定仓库下载该私有 jar 包
* 与 Gitlab Runner 集成, 自动同时发布到3个仓库
    - 拆分 nvm deploy 步骤: 先打包，再仅执行发布的步骤到3个私有仓库

## 构建并发布到不同的 maven 仓库

* [发布到阿里云效-制品仓库](https://packages.aliyun.com/repos/2117918-release-lbhiYo/guide)
* 需指定使用 `./others/m2-settings.xml` 文件作为 maven 的全局配置文件

```shell
# 构建
mvn clean install -Dgpg.skip=true


#################### 发布到 sonatype 官网仓库
# 发布 snapshot 版本
mvn jar:jar source:jar-no-fork javadoc:jar deploy:deploy -Poss -DversionSnapshot=-SNAPSHOT
# 发布 release 版本
mvn jar:jar source:jar-no-fork javadoc:jar deploy:deploy -Poss



#################### 发布到 阿里云效-制品仓库
# 发布 snapshot 版本
mvn jar:jar source:jar-no-fork javadoc:jar deploy:deploy -Prdc -DversionSnapshot=-SNAPSHOT
# 发布 release 版本
mvn jar:jar source:jar-no-fork javadoc:jar deploy:deploy -Prdc




#################### 发布到 个人nexus3私有仓库
# 发布 snapshot 版本
mvn jar:jar source:jar-no-fork javadoc:jar deploy:deploy -Pxbr -DversionSnapshot=-SNAPSHOT
# 发布 release 版本
mvn jar:jar source:jar-no-fork javadoc:jar deploy:deploy -Pxbr
```

## 其他说明

* 如何部署: `mvn clean deploy`, 需在命令行中输入 GPG 密钥的密码
* 发布成功后可在以下链接确定
    - sonatype 官方仓库: https://s01.oss.sonatype.org/#nexus-search;quick~io.github.saktools
    - 阿里云效 maven: https://maven.aliyun.com/mvn/search
        + 需手动搜索 io.github.saktools
        + 目前存在新发布版本未同步的情况, 只有 0.0.1 版本


## 发布到 `s01.oss.sonatype.org` 成功

* ~/.m2/setting.xml 配置如下所示:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <pluginGroups></pluginGroups>
    <proxies></proxies>
    <profiles></profiles>

    <servers>
        <server>
            <id>oss</id>
            <username>saktools</username>
            <password><![0810&XbrHz1118]]></password>
        </server>
    </servers>

    <mirrors>
        <mirror>
            <id>alimaven</id>
            <name>Aliyun Maven</name>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
</settings>
```

* 项目内 `pom.xml` 如下所示:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>io.github.saktools</groupId>
  <artifactId>utils</artifactId>
  <version>0.0.5</version> <!-- 版本里面不要带有-SNAPSHOT字样，否则上传后可能无法在仓库找到并发布 -->
  <name>utils</name>
  <description>一款实用的 java 工具库</description>
  <url>https://github.com/saktools/java-utils</url>

  <packaging>jar</packaging>

  <properties>
    <java.version>11</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <!-- 仓库地址配置信息 -->
	<distributionManagement>
		<snapshotRepository>
			<id>oss</id> <!-- id 要与setting.xml server id一致 -->
      		<url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
		</snapshotRepository>
		<repository>
			<id>oss</id> <!-- id 要与setting.xml server id一致 -->
      		<url>https://s01.oss.sonatype.org/content/repositories/releases</url>
		</repository>
	</distributionManagement>

  	<!-- 发布到oss.sonatype.org需要的组件 -->
	<build>
		<plugins>
			<!-- java source 生成插件 -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-source-plugin</artifactId>
				<version>2.2.1</version>
				<executions>
					<execution>
						<id>attach-sources</id>
						<goals>
							<goal>jar-no-fork</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
      		<!-- java doc 生成插件 -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-javadoc-plugin</artifactId>
				<version>3.2.0</version>
				<executions>
					<execution>
						<id>attach-javadocs</id>
						<goals>
							<goal>jar</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<!-- gpg 签名插件 -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-gpg-plugin</artifactId>
				<version>1.5</version>
				<executions>
					<execution>
						<id>sign-artifacts</id>
						<phase>verify</phase>
						<goals>
							<goal>sign</goal>
						</goals>
						<configuration>
							<gpgArguments>
								<arg>--pinentry-mode</arg>
								<arg>loopback</arg>
							</gpgArguments>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>


  <!-- 许可证信息 -->
  <licenses>
		<license>
			<name>The Apache Software License, Version2.0</name>
			<url>http://www.apache.org/licenses/</url>
			<distribution>repo</distribution>
		</license>
	</licenses>
  <!-- 托管仓库信息 -->
	<scm>
		<url>https://github.com/saktools/java-utils</url>
		<connection>scm:git:https://github.com/saktools/java-utils</connection>
		<developerConnection>scm:git:git@github.com/saktools/java-utils.git</developerConnection>
		<tag>HEAD</tag>
	</scm>
  <!--开发者信息-->
	<developers>
		<developer>
			<name>sakutils</name>
			<email>sakutils@outlook.com</email>
			<url>https://github.com/saktools</url>
			<roles>
				<role>Developer</role>
			</roles>
			<timezone>+8</timezone>
		</developer>
	</developers>
</project>
```
