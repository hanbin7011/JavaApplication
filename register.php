<?php
/**
 * Created by PhpStorm.
 * User: hanbinpark
 * Date: 6/4/18
 * Time: 12:01 PM
 */



if(empty($_REQUEST["username"]) || empty($_REQUEST["password"]) || empty($_REQUEST["email"]) || empty($_REQUEST["fullname"])){
    $returnArray["status"] = "400";
    $returnArray["message"] = "Missing required information";
    echo json_encode($returnArray);
    return;
}

$username = htmlentities($_REQUEST["username"]);
$password = htmlentities($_REQUEST["password"]);
$email = htmlentities($_REQUEST["email"]);
$fullname = htmlentities($_REQUEST["fullname"]);

$salt = openssl_random_pseudo_bytes(20);
$secured_password = sha1($password . $salt);

$file = parse_ini_file("../../Han.ini");

$host = trim($file["dbhost"]);
$user = trim($file["dbuser"]);
$pass = trim($file["dbpass"]);
$name = trim($file["dbname"]);

require("secure/access.php");
$access = new access($host, $user, $pass, $name);
$access->connect();

$result = $access->registerUser($username, $secured_password, $salt, $email, $fullname);

if($result){

    $user = $access->selectUser($username);


    $returnArray["status"] = "200";
    $returnArray["message"] = "Successfully register";
    $returnArray["id"] = $user["id"];
    $returnArray["username"] = $user["username"];
    $returnArray["email"] = $user["email"];
    $returnArray["fullname"] = $user["fullname"];
    $returnArray["ava"] = $user["ava"];

    //include email.php
    require ("secure/email.php");

    $email = new email();

    $token = $email->generateToken(20);

    $access->saveToken("emailTokens", $user["id"], $token);

    $details = array();
    $details["subject"]= "Email confirmation on Han";
    $details["to"] = $user["email"];
    $details["fromName"] = "hAn Support";
    $details["fromEmail"] = "han.help.contact@gmail.com";

    $template = $email->confirmationTemplate();

    $template = str_replace("{token}", $token, $template);

    $details["body"] = $template;
    $email->sendEmail($details);

}else{
    $returnArray["status"] = "400";
    $returnArray["message"] = "Could not register with provided information";


}

$access->disconnect();

echo json_encode($returnArray);

?>