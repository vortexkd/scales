# scales
クローラーの最終課題

スケールズはクロームブラウザーの画面上に小さなdivをあけ、その中にユーザーの行動に対してのコンテンツを表示します。
コンテンツはsnopesという反フェークニュースサービスとよく知られてるGuardianという新聞をクロールした結果です。

三つの部分にわけてあります、extensionのフォルダーがクローム用です。
index.phpはちょっと適当なローカルホースとのデモのためのphpファイルです。クライアントからの変数をJavaに投げるだけです（セキュリティ問題あり）。

Javaのフォルダのなかはもちろん、裏のJavaに使われる部分です。