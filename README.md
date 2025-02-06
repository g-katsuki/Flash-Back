EC2でやること
```bash
cd flashcard-app

docker-compose down

docker-compose pull

docker-compose up -d
```

## 起動

`./gradlew clean build -x test`

`docker-compose up --build `

## 終了

データを保持したまま終了する場合：
```
docker-compose down
```

データを完全に削除する場合：
```
docker-compose down -v
```

## 確認

docker psで以下のようにアプリケーションとDBが立ったことを確認

```
go@KatsukinoMacBook-Air Back % docker ps
CONTAINER ID   IMAGE            COMMAND                  CREATED         STATUS                   PORTS                    NAMES
0a2c6b8f21e7   back-app         "java -jar app.jar"      3 minutes ago   Up 3 minutes             0.0.0.0:8080->8080/tcp   back-app-1
706ef2af9e5c   mariadb:latest   "docker-entrypoint.s…"   3 minutes ago   Up 3 minutes (healthy)   0.0.0.0:3307->3306/tcp   back-db-1
```

## DockerHubへのプッシュ

0. 簡略コマンド
```
./gradlew clean build -x test
docker buildx build --platform linux/amd64 -t gkatsuki22/back-app:latest --push .
```

1. アプリケーションのビルド
```
./gradlew clean build -x test
docker-compose build
```

2. DockerHubにログイン
```
docker login
```

3. イメージにタグを付ける
```
docker tag back-app:latest gkatsuki22/flashcard-app:latest
```

4. イメージをプッシュ
```
docker push gkatsuki22/flashcard-app:latest
```

### 他の環境での実行

1. docker-compose.ymlのイメージを変更
```yaml
services:
  app:
    image: gkatsuki22/flashcard-app:latest
```

2. コンテナの起動
```
docker-compose up -d
```

## データベース

### マイグレーション
データベースのスキーマは自動的に作成されます。初期データは`src/main/resources/db/migration`に格納されています。

### バックアップとリストア

#### ローカル環境
バックアップの作成：
```
docker exec back-db-1 mariadb-dump -u root -pQwqwqw12!!?? flashcard_db > backup.sql
```

#### EC2環境
1. EC2上でバックアップを作成：
```
docker exec back-db-1 mariadb-dump -u root -pQwqwqw12!!?? flashcard_db > ~/backup.sql
```

2. ローカルPCへバックアップをダウンロード：
```
scp -i ~/.ssh/your-key.pem ec2-user@your-ec2-ip:~/backup.sql ./
```

3. バックアップからリストア（必要な場合）：
```
scp -i ~/.ssh/your-key.pem ./backup.sql ec2-user@your-ec2-ip:~/
cat ~/backup.sql | docker exec -i back-db-1 mariadb -u root -pQwqwqw12!!?? flashcard_db
```

## EC2環境での実行

### 1. EC2インスタンスのセットアップ

1. EC2インスタンスを起動
   - Amazon Linux 2023を推奨
   - セキュリティグループで以下のポートを開放：
     - 22 (SSH)
     - 443 (HTTPS)
     - 3307 (MariaDB - 必要な場合のみ)

2. Docker環境のセットアップ
```bash
# 必要なパッケージのインストール
sudo yum update -y
sudo yum install -y docker git

# Dockerの起動と自動起動設定
sudo systemctl start docker
sudo systemctl enable docker

# 現在のユーザーをdockerグループに追加
sudo usermod -aG docker ec2-user
# 一度ログアウトして再ログイン

# Docker Composeのインストール
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

# dockerにログイン
```
docker login
```

### 2. アプリケーションのデプロイ

1. 設定ファイルの準備
```bash
mkdir flashcard-app
cd flashcard-app

# docker-compose.ymlの作成
cat << EOF > docker-compose.yml
version: '3.8'

services:
  app:
    image: gkatsuki22/back-app:latest
    ports:
      - "443:443"  # HTTPSポートに変更
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://db:3306/flashcard_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: Qwqwqw12!!??
      SERVER_SSL_KEY_STORE: /ssl/keystore.jks
      SERVER_SSL_KEY_STORE_PASSWORD: Qwqwqw12!!??
      SERVER_SSL_KEY_ALIAS: tomcat
      SERVER_PORT: 443
    volumes:
      - ./ssl:/ssl  # 証明書をマウント
    restart: on-failure

  db:
    image: mariadb:latest
    environment:
      MYSQL_ROOT_PASSWORD: Qwqwqw12!!??
      MYSQL_DATABASE: flashcard_db
      MYSQL_USER: user
      MYSQL_PASSWORD: password
    ports:
      - "3307:3306"
    volumes:
      - ./docker/mysql/data:/var/lib/mysql
      - ./src/main/resources/db/migration:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD", "mariadb", "-u", "root", "-pQwqwqw12!!??", "-e", "SELECT 1"]
      interval: 5s
      timeout: 5s
      retries: 5
      start_period: 30s
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

volumes:
  mariadb_data:
    driver: local
EOF

# データディレクトリの作成
mkdir -p docker/mysql/data
```

2. アプリケーションの起動

```bash
docker-compose pull

docker-compose up -d
```

3. ログの確認
```bash
docker-compose logs -f
```

### 3. 動作確認

ローカル環境：
```
http://localhost:8080
```

本番環境：
```
https://[EC2のパブリックIP]
```

## HTTPS設定

### 1. お名前.comでドメインを取得
1. お名前.comでドメインを取得

### 2. 固定IP設定
1. AWSのEC2インスタンスにElastic IPを設定

### 3. ドメインのDNS設定
1. お名前.comのドメイン管理画面でドメインのDNS設定を行う

### 4. SSL証明書の準備
1. コマンドで証明書を取得(お名前.comのドメイン管理画面でSSL証明書を取得することもできる？)
2. 取得したSSL証明書をEC2インスタンスに配置


Open Router使用
# EC2上で環境変数を設定
echo 'OPENROUTER_API_KEY=[APIキー]' >> ~/.bashrc
source ~/.bashrc

# docker-compose.ymlで環境変数を参照
OPENROUTER_API_KEY=${OPENROUTER_API_KEY} docker-compose up -d