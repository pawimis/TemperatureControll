<?php
function get_random_string($valid_chars, $length)
{
    $random_string = "";
    $num_valid_chars = strlen($valid_chars);
    for ($i = 0; $i < $length; $i++)
    {
        $random_pick = mt_rand(1, $num_valid_chars);
        $random_char = $valid_chars[$random_pick-1];
        $random_string .= $random_char;
    }
    return $random_string;
}
if($_SERVER['REQUEST_METHOD'] == 'POST'){
 require_once('vars.php');
	 $dbc = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME)
		or die('No SQL server connection');
	$user_login = $_POST['user_login'];
	$user_password = $_POST['password'];
	if(!empty($user_login) && !empty($user_password)){
                $query = "SELECT * FROM UserDatabase WHERE Username = '$user_login' AND Password = '$user_password'";
		$check = mysqli_fetch_array(mysqli_query($dbc,$query));
		if(!isset($check)){
                    $query = "INSERT INTO UserDatabase(Date,Username,Password)".
                            "VALUES(NOW(),'$user_login','$user_password')";
                    
                    mysqli_query($dbc,$query)
                            or die('ERROR1');
                    echo "new User";
                }else{
                    echo"OK";
                }
	}
	else{
		echo "ERROR";
	}
}else{
	echo "non POST err";
}

?>
