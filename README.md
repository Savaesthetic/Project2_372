# Project2_372
Creating a translator in java for a self-made language.

# Warning
## Be careful with spaces as the interpreter is not forgiving when it comes to them.
# Running the program (Since it's an interpreter)
To run the program simply use: java ./Translator.java ./Filename.txt (Additional Command Line Arguments)<br/>
Running the example program Program1.txt with arguments 2, 5, and 20 would look like:<br/>
java Translator.java Program1.txt 2 5 20<br/>

## Grammar
\<program\>:==\<statement_block\><br/><br/>

\<statement_block\>:== \<statement\>|<br/>
\<statement\>\<delimiter\>\<statement_block\><br/><br/>

\<delimiter\>:==?|!|@|#|$|%<br/><br/>

\<statement\>:==\<variable_assignment\>|<br/>
\<conditional\>|<br/>
\<loop\>|<br/>
\<print\>|<br/>
\<exit\><br/><br/>

\<variable_assignment\>::=var \<variable\>:\<integer\>|<br/>
var \<variable\>:-\<integer\>|<br/>
var \<variable\>:\<variable\>|<br/>
var \<variable\>:\<arithmetic_exp\><br/><br/>

\<integer\>::=0|<br/>
1|<br/>
0\<integer\>|<br/>
1\<integer\><br/><br/>

\<variable\>::= [A-Z]|<br/>
[A-Z]\<variable\><br/><br/>

\<print\>::=console <>|<br/>
console <<string_literal>>|<br/>
console <\<element\>><br/><br/>

\<string_literal\>::='\<string\>'<br/><br/>

\<string\>::=[^?!@#$%] (Any character other than (?!@#$%) because they are used for splitting statements.)<br/><br/>

\<element\>::=\<variable\>|\<integer\><br/><br/>

\<arithmetic_exp\>::=math\<\<operator\>, \<element\>, \<element\>\><br/><br/>

\<operator\>:==add|sub|mul|div|mod<br/><br/>

\<boolean\>false|true<br/><br/>

\<boolean_root\>::=\<boolean\>|\<comp_exp\><br/><br/>

\<boolean_exp\>::=\<boolean_root\> and \<boolean_exp\>|<br/>
\<boolean_root\> or \<boolean_exp\>|<br/>
not \<boolean_exp\>|<br/>
\<boolean_root\><br/><br/>

\<comp_exp\>::=\<element\> !eq \<arithmetic_exp\>|<br/>
\<element\> eq \<arithmetic_exp\>|<br/>
\<element\> lt \<arithmetic_exp\>|<br/>
\<element\> gt \<arithmetic_exp\>|<br/>
\<element\> lt= \<arithmetic_exp\>|<br/>
\<element\> gt= \<arithmetic_exp\>|<br/>
\<element\><br/><br/>

\<conditional\>::= do <<statement_block>> if <<boolean_exp>> otherwise do <<statement_block>><br/><br/>

\<loops\>::= loop \<\<element\>->\<element\>, \<arithmetic_exp\>> {\<statement_block\>}<br/><br/>

\<command_line\>::=CLONE|CLTWO|CLTHREE|CLFOUR|CLFIVE (CLZERO Not available because arg[0] is occupied by program file. Only up to 5 command line arguments)<br/>
