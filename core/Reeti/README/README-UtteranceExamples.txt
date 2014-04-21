Note 1: You can use the following five actual examples to write new utterances.
Note 2: Pay attention to the usage of "break time" and "delay" between different types of verbal and non-verbal utterances.


Example 1 - [Utterance includes text + wait + text.]:
This is the first part of my utterance. &lt;break time=\&#34;2s\&#34;/&gt; Here is the second part of my utterance. &lt;break time=\&#34;2s\&#34;/&gt;

Example 2 - [Utterance includes text + wait + text + wait + head nod + wait + head nod + wait.]:
I am testing mark-ups with different durations. &lt;break time=\&#34;1s\&#34;/&gt; Here is my head nod. &lt;break time=\&#34;1s\&#34;/&gt; &lt;HEADNOD/&gt; &lt;DELAY MS=\&#34;1000\&#34;/&gt; &lt;break time=\&#34;2s\&#34;/&gt; Here is my second head nod. &lt;break time=\&#34;1s\&#34;/&gt; &lt;HEADNOD/&gt; &lt;break time=\&#34;2s\&#34;/&gt;

Example 3 - [Utterance includes text + wait + text + wait + head nod + wait + happy + wait + neutral.]:
I am testing mark-ups with different durations. &lt;break time=\&#34;1s\&#34;/&gt; Here is my head nod. &lt;break time=\&#34;1s\&#34;/&gt; &lt;HEADNOD/&gt; &lt;DELAY MS=\&#34;1000\&#34;/&gt; &lt;break time=\&#34;2s\&#34;/&gt; I can smile. &lt;break time=\&#34;1s\&#34;/&gt; &lt;FACE EXPR=\&#34;SMILE\&#34;/&gt; &lt;DELAY MS=\&#34;2000\&#34;/&gt; &lt;break time=\&#34;3s\&#34;/&gt; &lt;FACE EXPR=\&#34;WARM\&#34;/&gt;

Example 4 - [Utterance includes text + wait + text + wait + head nod + wait + happy + wait + text + wait + neutral.]:
I am testing mark-ups with different durations. &lt;break time=\&#34;1s\&#34;/&gt; Here is my head nod. &lt;break time=\&#34;1s\&#34;/&gt; &lt;HEADNOD/&gt; &lt;DELAY MS=\&#34;1000\&#34;/&gt; &lt;break time=\&#34;2s\&#34;/&gt; I can smile. &lt;break time=\&#34;1s\&#34;/&gt; &lt;FACE EXPR=\&#34;SMILE\&#34;/&gt; &lt;DELAY MS=\&#34;2000\&#34;/&gt; &lt;break time=\&#34;3s\&#34;/&gt; This is me talking while I am smiling. &lt;DELAY MS=\&#34;1000\&#34;/&gt; &lt;break time=\&#34;2s\&#34;/&gt; &lt;FACE EXPR=\&#34;WARM\&#34;/&gt;

Example 5 - [Utterance includes text + wait + text + wait + head nod + wait + happy + wait + text + wait + neutral + text + wait + sad + wait + neutral + text + wait + gaze + wait + neutral.]:
I am testing mark-ups with different durations. &lt;break time=\&#34;1s\&#34;/&gt; Here is my head nod. &lt;break time=\&#34;1s\&#34;/&gt; &lt;HEADNOD/&gt; &lt;DELAY MS=\&#34;1000\&#34;/&gt; &lt;break time=\&#34;2s\&#34;/&gt; I can smile. &lt;break time=\&#34;1s\&#34;/&gt; &lt;FACE EXPR=\&#34;SMILE\&#34;/&gt; &lt;DELAY MS=\&#34;2000\&#34;/&gt; &lt;break time=\&#34;3s\&#34;/&gt; This is me talking while I am smiling. &lt;DELAY MS=\&#34;1000\&#34;/&gt; &lt;break time=\&#34;2s\&#34;/&gt; &lt;FACE EXPR=\&#34;WARM\&#34;/&gt; I can also frown. &lt;break time=\&#34;1s\&#34;/&gt; &lt;FACE EXPR=\&#34;CONCERN\&#34;/&gt; &lt;DELAY MS=\&#34;4000\&#34;/&gt; &lt;break time=\&#34;5s\&#34;/&gt; &lt;FACE EXPR=\&#34;WARM\&#34;/&gt; I can change my gaze direction too. &lt;break time=\&#34;1s\&#34;/&gt; &lt;GAZE horizontal=\&#34;-1\&#34; vertical=\&#34;1\&#34;/&gt; &lt;DELAY MS=\&#34;5000\&#34;/&gt; &lt;break time=\&#34;6s\&#34;/&gt; &lt;FACE EXPR=\&#34;WARM\&#34;/&gt;

