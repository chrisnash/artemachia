artemachia
==========

Battle simulator and calcuiator for BattleSpace.

Introduction
------------

Welcome to the artemachia project, a simulator for the fleet battles you'll experience in the Atlus Online title *BattleSpace*.
Both fledgling commanders and experienced players often grapple with the core question: exactly which ships work best against which
enemy formations? Is my commander choice even capable of winning this battle, how do I minimize casualties, and how can I improve
my strategy?

The artemachia project attempts to help with these questions by simulating the battle mechanics as observed in BattleSpace. By
careful study and observation of replays, an *estimate* of what goes on under the covers is applied to attempt to calculate
not just the odds of success, but also other relevant factors such as domination score, ships remaining, the winning fleet you
can throw together in the shortest time, or even the replacement build time for those ships you lose.

As Sun Tzu is often quoted (from the Art Of War)

> If you know the enemy and know yourself, you need not fear the result of a hundred battles.

Let the artemachia project simulate thousands of battles for you, and help you in your quest.
 
Disclaimers
-----------

Firstly, the disclaimers. I do not have, nor have ever had, any connection with BattleSpace's authors, Index Digital Media
(Atlus Online) other than being an avid player of the game. The simulations here are merely educated guesswork based upon
observation and study of the game as it is played. Likewise, I accept no responsibility if you follow the advice of the
program and things don't turn ou as predicted; there are sufficient random elements in the game to make all but the simplest
battles very hard to predict.

The data supplied with the program is accurate to my knowledge, but only covers those features of the game that I have unlocked
at the current time. While every endeavor has been made to ensure the data is correct and accurate, I would greatly welcome
the report and contribution of patches for any errors and omissions. Likewise, while any developer is free to fork this project,
I would appreciate if any contributions were offered for a push to this master repo.

Finally, a few words of thanks, to the members of The Avalon guild on the Pinwheel server, who have had to tolerate my ramblings
considerably during season one, and to Avalon officer Halifix for her informative posts and observations which have helped in the
creation of this simulator.

Terminology
-----------

Let's get started with a quick primer. You'll need Java installed to run the simulator, and it needs to be run from a Terminal, or
a command prompt, or a bash shell, or something like that. If you're stuck already, then this project probably isn't for you. No
fancy graphical interfaces here, although I would of course welcome anyone contributing one :)

The first issue you'll face when using this problem is how to enter all the data needed to accurately describe your player
capabilities, and those of your enemies. I've elected to use a simple shorthand for the names of the elements you will encounter
in the game. After a while, these will become second nature to you.

### Greek letters ###

Anywhere a Greek letter is used in the game, \alpha;, \beta;, \gamma;, \delta;, \epsilon;, I've gone with the simplest approach:
use a, b, c, d, e. Yes, I know \gamma; isn't really c.

### Ship names ###

I've gone for the simplest abbreviations I can think of for ship names, so "M-II Mass Produced Ship b" becomes *m2mpb*. No need
for the "ship" part, and just used the initials. This works, up to a point. Some abbreviations made this way aren't unique. For
example "S-I Armored Ship a" becomes *s1sa* (the s standing for 'shield'), and "L-1 Domination Ship a" becomes *l1na* (the n
reflecting the hard n in "domaination"). If you don't like these, you can go ahead and change them in the data files.

For enemy ships, the initials don't work, because so many begin with "a". In this case, I used three letters for the ship name,
so for instance "L Archenemy Ship b" becomes *larcb*. Yes, that amusingly means you have ships called "santa" and "maria". :)

### Formations ###

I've endeavored to use formation names as they are, without spaces, and preferably lower case. For example telescopium, leominor.
If a formation is missing from the data, you can enter it longhand using a 35-character code, reading top to bottom, left to
right. For example, ..X..X......X....XX...........X.... is the Octans formation that appears on asteroids.

### Planet data ###

Often you'll need to specify a planet, which will automatically select the formation and enemy fleets. Resource planets are of
the for "l2-4", which means level 2, type 4. The types correspond to their positions on the crib sheet at
http://battlespace.somee.com/planets.asp . If you don't have that page around (or, as has happened recently, the page is
no longer online), you can enter the level and the planet layout, such as 2,Mmmeg for that same planet. Asteroids can be
entered as, for example "a4"; giant planets, for example, "g8". If you want to enter a fleet manually, you can, by specifying
the formation, in long or short form, followed by the ships. That same level 2 planet can be entered as
"...X.....XX......X.................,lanta,sanab,santa,lanta", if you want. Not that useful, but it may come in handy for
things like pirates and interception fleets.

### Commander skills ###

These are a bit difficult to abbreviate to short codes, so I've taken a simple approach. Remove useless words like
"type" and "level" and just fold the rest down. "M-Type Ionizer Skill Level 1" becomes *mionizer1*. Easy.

### What you'll need to know ###

The simulator needs a fair amount of data from you for it to function correctly. Here's the basic list:

* what ships you currently have available
* what their upgrades are
* what your military skill tree level is (points invested)
* what your union's level in "armor research" is (affects your ship's durability)

Yes, that's a lot of data, which you need to supply just once, in the player_data.txt file. Simply uncomment those ships you
own (by removing the # sign), and specify the four upgrade levels, reading top to bottom, as a number from 0000 (no upgrades)
to 3333 (fully upgraded in everything). The military skill and union armor should be self-explanatory. Just keep this file
updated as you progress through the game. And, of course, you may perform "what-if" scenarios, for example testing what would
happen if you got a certain upgrade, or if certain ships weren't available for you because you just didn't have room to build
a factory on that roid. Just remember, backup your main player file first!

### Useful tips and techniques ###

What you'll be doing a lot of when using the simulator is entering your commander data.