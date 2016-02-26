keytool -genkey -dname "cn=xiaorui, ou=Java Software, o=xiaorui, c=China" -alias xiaorui -keypass 123456 -storepass 123456 -validity 365 -keystore .\xiaorui
keytool -list -keystore .\xiaorui -storepass 123456
keytool -export -keystore .\xiaorui -storepass 123456 -file xiaorui.cer -alias xiaorui