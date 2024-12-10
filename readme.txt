header: content-type: application/json
url:http://localhost:8080/api/boardgames 
body raw:
[
    {
        "gid": 101,
        "name": "Catan",
        "year": 1995
    }
]

In railway -> Creat project
Add github project to railway
In railway add redis data

In railway, add the 4 to service variables to redis
replace the spring.data.redis.host with the .net url from setting
replace the spring.data.redis.port with the port from setting

In data, click connect -> public network -> raw cli to test ping