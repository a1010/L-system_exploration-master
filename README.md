gephi-toolki内のprocessingライブラリはバージョンが古いので注意

processingの機能だけを使う	→	lib_processing内のJARファイル
gephi + processingの機能を使う	→	lib内のgephi-toolkit.JAR

---
以下の文責者：井上(183310)</br>
過去の死滅ルール導入によるクラス依存関係が未整理のまま。</br>
研究の成果を簡単にまとめると、以下の通り。</br>

# 研究のまとめ
    < 取り組んだこと >
        数km範囲の広い道路網を想定した探索についての問題解決をした
    < 解決した問題点 >
        広い領域における道路網のフラクタル次元では局所的な複雑さが損なわれてしまう
    < 解決手法 >
        道路網を小さな領域に切り分けてフラクタル次元を計算する
## 研究の成果
    < 得られた経路の形状について >
        複雑でない箇所は粗く探索するため、従来より直線的な経路となった
    < 経路の長さ >
        従来より長い距離の経路となった
    < ゴール到達までの探索領域 >
        複雑でない箇所を粗く探索することで削減できた
    < 計算時間(ステップ数) >
        2×2の場合のみ削減できて、4×4以降では平均値を維持する結果となった
### 今後の課題・展望
* 適切な分割領域の大きさについての検討
* 分岐角度90度からさらに自由度高く
* 1ステップ当たりの分岐による移動距離を1pixelからさらに自由度高く
* フラクタル次元以外の複雑さ指標の検討
* 道路網の形状を考慮した複雑さ指標・アルゴリズムの検討