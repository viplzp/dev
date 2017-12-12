redis.call('INCRBY', KEYS[1], ARGV[1])
local current = redis.call('GET', KEYS[1])
local result ={}
result["@class"] = "io.flysium.framework.message.ResponseResult"
if tonumber(current)<0
then
    redis.call('DECRBY', KEYS[1], ARGV[1])
    current = redis.call('GET', KEYS[1])
    result["res_code"] = '9999'
    result["result"] = current
    local encodestr = cjson.encode(result)
    print(encodestr)
    return  encodestr
end
result["res_code"] = '0000'
result["result"] = current
local encodestr = cjson.encode(result)
print(encodestr)
return  encodestr