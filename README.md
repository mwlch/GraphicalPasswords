# Graphical Passwords

As part of a project on graphical passwords on smartphones, we implemented a number of graphical password schemes for Android. 
We used these implementations for our study in the following paper:

>  Schaub, F., Walch, M., Könings, B., & Weber, M. (2013, July).<br>
>  [Exploring the design space of graphical passwords on smartphones](http://dl.acm.org/citation.cfm?id=2501615).<br>
>  In Proceedings of the Ninth Symposium on Usable Privacy and Security (p. 11). ACM.<br>
>  [[PDF]](http://cups.cs.cmu.edu/soups/2013/proceedings/a11_Schaub.pdf)

We provide our implementations to the research and mobile developer communities to facilitate replication of research results and encoruage the use of graphical password schemes.

The different schemes have been implemented according to the respective descriptions in the original papers listed below. Please acknowledge us by citing our SOUPS paper when you make use of our implementations, and cite or and acknowledge the creators when referring to the respective schemes.

## Graphical Password Schemes

### [Open MIBA](src/de/uulm/graphicalpasswords/openmiba/README.md)
Android implementation of MIBA:
>  D. Ritter, F. Schaub, M. Walch, and M. Weber.<br>
>  MIBA: Multitouch image-based authentication on smartphones.<br>
>  In <i>Proc. CHI '13 Extended Abstracts</i>. ACM, 2013.
[PDF](http://www.uni-ulm.de/fileadmin/website_uni_ulm/iui.inst.100/institut/Papers/Prof_Weber/2013-CHI-EA-miba.pdf)

### [Open Pass-Go](src/de/uulm/graphicalpasswords/openpassgo/README.md)
Android implementation of Pass-Go:

>  H. Tao and C. Adams.<br> 
>  Pass-Go: A Proposal to Improve the Usability of Graphical Passwords.<br>
>  <i>Int. J. Network Security</i>, 7(2):273–292, 2008.
[Pass-Go website](http://passgo.ca/), [PDF](http://passgo.ca/ijns-2008-v7-n2-p273-292.pdf)

### [Open TAPI](src/de/uulm/graphicalpasswords/opentapi/README.md)
Android implementation of TAPI:

>  J. Citty and D. R. Hutchings.<br> 
>  TAPI: touch–screen authentication using partitioned images.<br>
>  Tech. Report 2010–1, Elon University, 2010.
[PDF](http://facstaff.elon.edu/dhutchings/papers/citty2010tapi.pdf)

### [Open UYI](src/de/uulm/graphicalpasswords/openuyi/README.md)
Android implementation of Use Your Illusion (UYI):

>  E. Hayashi, R. Dhamija, N. Christin, and A. Perrig.<br> 
>  Use Your Illusion: secure authentication usable anywhere.<br>
>  In <i>Proc. SOUPS '08</i>. ACM, 2008.
[DOI](http://dx.doi.org/10.1145/1408664.1408670), [PDF](http://cups.cs.cmu.edu/soups/2008/proceedings/p35Hayashi.pdf)
	

## Licence

    Copyright 2014 Marcel Walch, Florian Schaub

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
