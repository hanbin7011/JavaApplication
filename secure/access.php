<?php
/**
 * Created by PhpStorm.
 * User: hanbinpark
 * Date: 6/4/18
 * Time: 11:28 AM
 */

class access{

    //connection global variables
    var $host = null;
    var $user = null;
    var $pass = null;
    var $name = null;
    var $conn = null;
    var $result = null;


    function  __construct($dbhost, $dbuser, $dbpass, $dbname){

        $this->host = $dbhost;
        $this->user = $dbuser;
        $this->pass = $dbpass;
        $this->name = $dbname;

    }

    //connection function
    public function connect(){

        //establish connection and store it in $conn
        $this->conn = new mysqli($this->host, $this->user, $this->pass, $this->name);

        if(mysqli_connect_errno()){
            echo "Could not connect to database";
        }else{
            //echo "Conneted";
        }

        $this->conn->set_charset("utf8");
    }

    public function disconnect(){

        if($this->conn != null){
            $this->conn->close();
        }
    }

    public function registerUser($username, $password, $salt, $email, $fullname){

        //sql command
        $sql = "INSERT INTO users SET username=?, password=?, salt=?, email=?, fullname=?";

        //store query result in $statement
        $statement = $this->conn->prepare($sql);

        if(!$statement){
            throw new Exception($statement->error);
        }

        $statement->bind_param("sssss", $username, $password, $salt, $email, $fullname);

        $returnValue = $statement->execute();

        return $returnValue;

    }

    public function selectUser($username){

        $returnArray = null;

        $sql = "SELECT * FROM users WHERE username='".$username."'";

        $result = $this->conn->query($sql);


        if($result != null && (mysqli_num_rows($result) >= 1)){
            $row = $result->fetch_array(MYSQLI_ASSOC);

            if(!empty($row)){
                $returnArray = $row;
            }
        }

        return $returnArray;
    }

    public function selectUserViaEmail($email){

        $returnArray = null;

        $sql = "SELECT * FROM users WHERE email='".$email."'";

        $result = $this->conn->query($sql);


        if($result != null && (mysqli_num_rows($result) >= 1)){
            $row = $result->fetch_array(MYSQLI_ASSOC);

            if(!empty($row)){
                $returnArray = $row;
            }
        }

        return $returnArray;
    }


    //Save email confirmation message's token
    public  function  saveToken($table, $id, $token){

        //sql statement
        $sql = "INSERT INTO $table SET id=?, token=?";

        //prepare statement to be executed
        $statement = $this->conn->prepare($sql);

        if(!$statement){
            throw new Exception($statement->error);
        }

        //bind paramaters to sql statement
        $statement->bind_param("is", $id, $token);

        //launch / execute and store feedback in $returnValue
        $returnValue = $statement->execute();

        return $returnValue;

    }

    function getUserID($table, $token){

        $returnArray = null;
        //sql statement
        $sql = "SELECT id FROM $table WHERE token ='".$token."'";

        $result = $this->conn->query($sql);

        if($result != null && (mysqli_num_rows($result)>= 1)){
            $row = $result->fetch_array(MYSQLI_ASSOC);

            if(!empty($row)){
                $returnArray = $row;
            }
        }

        return $returnArray;
    }

    function  emailConfirmationStatus($status, $id){
        $sql = "UPDATE users SET emailConfirmed=? WHERE id=?";
        $statement = $this->conn->prepare($sql);

        if(!$statement){
            throw new Exception($statement->error);
        }

        $statement->bind_param("ii", $status,$id);

        $returnValue = $statement->execute();

        return $returnValue;
    }

    public function  updatePassword($id, $password, $salt){
        $sql = "UPDATE users SET password=?, salt=? WHERE id=?";
        $statement = $this->conn->prepare($sql);

        if(!$statement){
            throw new Exception($statement->error);
        }

        $statement->bind_param("ssi", $password,$salt ,$id);

        $returnValue = $statement->execute();

        return $returnValue;
    }

    function deleteToken($table, $token){
        $sql = "DELETE FROM $table WHERE token=?";

        $statement = $this->conn->prepare($sql);

        if(!$statement){
            throw new Exception($statement->error);
        }

        $statement->bind_param("s",$token);

        $returnValue = $statement->execute();

        return $returnValue;
    }

    public function getUser($email){
        $returnArray = array();

        $sql = "SELECT * FROM users WHERE email='".$email."'";

        $result = $this->conn->query($sql);

        if($result != null && (mysqli_num_rows($result)>=1)){
            $row = $result->fetch_array(MYSQLI_ASSOC);
        }

        if(!empty($row)){
            $returnArray = $row;
        }

        return $returnArray;
    }

}

?>