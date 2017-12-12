local curRemNum = tonumber(redis.call('decr', KEYS[1]));
if curRemNum < 0 then
 redis.call('set', KEYS[1],'0');
return false;
end 
return true;