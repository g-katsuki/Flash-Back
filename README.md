## 起動

`./gradlew clean build -x test`

`docker-compose up --build `

## 終了

`docker-compose down -v`

## 確認

docker psで以下のようにアプリケーションとDBが立ったことを確認

```
go@KatsukinoMacBook-Air Back % docker ps
CONTAINER ID   IMAGE            COMMAND                  CREATED         STATUS                   PORTS                    NAMES
0a2c6b8f21e7   back-app         "java -jar app.jar"      3 minutes ago   Up 3 minutes             0.0.0.0:8080->8080/tcp   back-app-1
706ef2af9e5c   mariadb:latest   "docker-entrypoint.s…"   3 minutes ago   Up 3 minutes (healthy)   0.0.0.0:3307->3306/tcp   back-db-1
```

## DockerHubへのプッシュ

1. DockerHubにログイン
```
docker login
```

2. イメージにタグを付ける
```
docker tag back-app:latest gkatsuki22/back-app:latest
```

3. イメージをプッシュ
```
docker push gkatsuki22/back-app:latest
```

