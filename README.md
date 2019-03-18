# 🖼 hashcode-2019

![](https://anchr.io/i/9wLgJ.png)

Solution for Google Hash Code 2019 Qualification Round. Problem statement can be found [here](https://storage.googleapis.com/coding-competitions.appspot.com/HC/2019/hashcode2019_qualification_task.pdf).

**Please note:** This solution did not actually participate in 2019's qualification round, but was created afterwards. It exceeds the runtime constraints and therefore would not have been suitable as a submission.

## Approaches
**1. Naive approach** (`GreedyNaive.scala`) (_O(n²)_ worst case): 
  1. Construct slides from vertical pictures by iteratively combining those with most tags
  2. For every slide, iteratively find slide with highest _interest_ factor in a greedy-like way

**2. Filtered naive approach** (`GreedyFiltered.scala`) (_O(n*m)_ worst case):
  * Similar to first approach, but in second step only slides with a similar number of tags as the current slide are taken into account

## Usage
* Adapt input file path in `Solution.scala`
* `sbt run`

## Result
### Naive approach
* `a_example.txt`: 2 points, 0.14 seconds
* `b_lovely_landscapes.txt`: 202707 points, 4.68 hours
* `c_memorable_moments.txt`: 1535 points, 2.56 seconds
* `d_pet_pictures.txt`: 434189 points, 3.91 hours
* `e_shiny_selfies.txt`: 406066 points, 3.69 hours

**Total:** 1,044,499 points, 12.28 hours ☠️
**Est. rank:** 83 / 6671 (1.2 %)

### Filtered naive approach
* `a_example.txt`: 2 points, 0.13 seconds
* `b_lovely_landscapes.txt`: 81306 points, 1.57 hours
* `c_memorable_moments.txt`: 1489 points, 2.65 seconds
* `d_pet_pictures.txt`: 406488 points, 1.96 hours
* `e_shiny_selfies.txt`: 361380 points, 0.7 hours

**Total:** 850,665 points, 4.23 hours ☠️
**Est. rank:** 450 / 6671 (6.7 %)

The results were produced in parallel fashion on a 12-core CPU.

## Conclusion
The naive approach is totally unrealistic due to extensive runtime. The filtered naive approach essentially trades off score vs. runtime. By tweaking its parameters, runtime could probably be pushed down to ~ 3 hours while losing total score. All in all, a more sophisticated solution would be appropriate...

## License
MIT
