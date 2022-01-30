(ns advent-of-code.day22-part1-test)

(def example
  ["on x=10..12,y=10..12,z=10..12"
   "on x=11..13,y=11..13,z=11..13"
   "off x=9..11,y=9..11,z=9..11"
   "on x=10..10,y=10..10,z=10..10"])

;(-> example parse-lines reboot-reactor cubes-on)
;=> 39