# OandaAutoTrader
カレントディレクトリーにアウトプットログ用のデータoutput.log(任意で名称変更可)と、
パスワード取得用の"OandaAutoTrader.properties"を用意します。
＜例＞
user/hoge/
- output.log
- OandaAutoTrader.properties

output.logは空のtextファイルを準備。
OandaAutoTrader.propertiesは下記を参考に、テキストエディタ等で必要な情報を書き込む。

OandaAutoTrader.properties<br>
UserName = oandaから取得したユーザーネーム<br>
PassWord = パスワード<br>
AccountID = 口座のアカウントID<br>

ターミナルから起動するには、"output.log" "OandaAutoTrader.properties"を準備したカレントディレクトリーに移動する。<br>
$ cd user/hoge/<br>
$ java -jar "任意のディレクトリ/OandaAutoTrader.jar" 2>output.log<br>

CAUTION
----------------------------------------
プロパティーデータのセキュリティーは各自で対策してください。
製作者は一切の責任を負いません。
