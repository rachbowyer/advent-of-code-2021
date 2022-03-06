# [advent-of-code 2021](https://adventofcode.com)




For the first time ever, I tackled Advent of Code. And finally, on the 6 March I 
completed it. In total my solutions comprised 2348 NCLOC of Clojure.

I really enjoyed tackling the challenges. I tried to do each one on my own, 
although if I got stuck I then went online to look for hints. After I completed
the problems I found it interesting and educational to compare my solutions to 
other people's.

Day 7. I used what I thought was a neat mathematical trick to answer the question
       and although I got the right answer, it turned out my mathematical trick
       wasn't actually robust.

Day 15. I initially tried a dynamic approach which didn't and could never work. 
        Stuck, I looked online and saw that other people were using uniform cost
        search or A* search. That hint was all I needed - and I solved it using
        A*.

Day 17. I solved this but had some theoretical concerns with my solution. Looking
        online I learnt that my solution was valid and the reason for it.

Day 22. I correctly realised that part 2 could be solved by keeping a list of
        disjoint cubes. I thought there must be an easier way, and read that 
        people were talking about cubes with negative volumes. I took this 
        idea and turned it into a solution.

Day 23. I solved the first part using A* search. My code was a bit clunky and 
        slow, but did the job. The second part was more of the same, but I would
        have had to adjust and optimise my code so I skipped it.

Day 24. Before I had finished reverse engineering MONAD I read about people 
        talking about a stack of mult 26 and div 26 commands. At this stage it
        did not make much sense to me. But once I had finished the reverse 
        engineering, I not only learnt what was meant but how to use this 
        inherent structure to find the highest and lowest serial numbers. The
        solution was easy enough to find by hand so I didn't need to code
        a program.

Other solutions to Advent of Code 2021 in Clojure are available at:

* https://github.com/wevre/advent-of-code/tree/master/src/advent_of_code/2021
* https://github.com/kfirmanty/advent-of-code-2021
