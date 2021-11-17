# Project2_372
Creating a translator in java for a self-made language.

## Grammar
\<program\>:==\<statement\>?|\<statement\>?\<program\>
\<statement\>:==<variable_assignment>|<conditional>|<loop>|<print>
\<sub_statements>:==<statement>|<statement>,<sub_statements>
\<integer>::=0|1|0<integer>|1<integer>
\<variable>::= [A-Z]|[A-Z]<variable>
\<element>::=<variable>|<integer> // Should I include string literal in here?
\<variable_assignment>::=<variable>:<integer>
\<arithmetic_exp>::=<element>+<arithmetic_exp>|
\<element>-<arithmetic_exp>|
\<element>*<arithmetic_exp>|
\<element>/<arithmetic_exp>|
\<element>%<arithmetic_exp>|
\<element>
\<boolean>::=false|true // true is false and false is true
\<boolean_exp>::= // TODO
\<comp_exp>::= // TODO
\<conditional>::= do <<sub_statements>> if <<boolean_exp>> otherwise do <<boolean_exp>>
\<loops>::= loop(<element>-><element>, increment <integer>) // while and for necessary? just for?
\<print>::=console <>|<<string_literal>>|<<element>> // need to be able to print anything else? Do I have to define string_literal?
\<command_line>::= // TODO Unsure how to do this ask for help