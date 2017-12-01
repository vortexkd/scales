<?php
header('Content-Type: text/html; charset=UTF-8');
session_start();

$java_path = "com.company.Main 2>&1";

$_SESSION['argument'] = "trump";
if(isset($_POST['url'])) {
	$_SESSION['argument'] = escapeshellarg($_POST['url']);
}
if(isset ($_POST['search_selection'])) {
	$_SESSION['argument'] = escapeshellarg($_POST['search_selection']);
	echo $_SESSION['argument'];
}
$output = [];
chdir ("java/Scales/");
exec("java ".$java_path." ".$_SESSION['argument'], $output);

if (isset ($_SESSION['url'])) {
	exec("java ".$java_path." ".$_SESSION['url'], $output); //not secure, will fix later.
}
elseif (isset($_SESSION['search_selection'])) {
	exec("java ".$java_path." ".$_SESSION['search_selection'], $output); //not secure, will fix later.
}
print ("<meta charset='UTF-8' />"); 
print("<div id='content' style='width:300px;background-color:#FFFDD0;padding:5px;'>");
foreach ($output as $line) {
	print($line);
}
print("</div>");
?> 