<?php
/**
 * Created by PhpStorm.
 * User: hanbinpark
 * Date: 6/7/18
 * Time: 10:21 AM
 */



if(empty($_GET["token"])){
    echo 'Missing required information';
}

$token = htmlentities($_GET["token"]);

$file = parse_ini_file("../../../Han.ini");

$host = trim($file["dbhost"]);
$user = trim($file["dbuser"]);
$pass = trim($file["dbpass"]);
$name = trim($file["dbname"]);

require ("../secure/access.php");
$access = new access($host, $user, $pass, $name);
$access->connect();

$id = $access->getUserID("emailTokens", $token);

if(empty($id["id"])){
    echo 'User with this token is not found';
    echo $token;
    return;
}

$result = $access->emailConfirmationStatus(1, $id["id"]);

if($result){
    $access->deleteToken("emailTokens", $token);
    echo 'Thank you! Your email is now confirmed';
}

$access->disconnect();


?>