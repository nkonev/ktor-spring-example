```
docker run --rm -d -p 27017:27017  mongo:5.0.5
```

# Querying
```
curl -i -X POST -H "Accept: application/json" 'http://localhost:8098/customer'
```

# Logging in
```
curl -i -X GET 'http://localhost:8098/login'
```

# Getting session info
```
curl -i -X GET -H 'Cookie: user_session=48377d001c21a99547290c00395dd461' 'http://localhost:8098/session'
```

# Logout
```
curl -i -X GET -H 'Cookie: user_session=48377d001c21a99547290c00395dd461' 'http://localhost:8098/logout'
```

# Open Mongo
```
docker exec -it ktor-sandbox_mongo_1 mongosh
```

# Stopping
```
kill -2 $(ps -ef | grep SandboxApplication | grep -v 'grep' | awk '{print $2}')
```

# Save to mongo
```
curl -i -X POST 'http://localhost:8098/mongo'
```

# Get from mongo
```
curl -i  'http://localhost:8098/mongo'
```
