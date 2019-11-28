# Calorie Center
#### A simple way to track and log your calories.   
You can manage and edit your current calorie intake,  
and track your progress over time as you log your daily caloric intake.  
<p align="center">
<img src="https://i.imgur.com/4JkErKh.gif "alt="demogif" width="200">  
</p>  

# How it works
Your TDEE is the daily amount of calories that you burn, this is calculated for you during the setup in the app.
Depending on if you know your Body Fat % or not, the following two forumulas will be used:

### Mifflin St. Jeor (Unknown BF%)  
**Men**
`10 x weight (kg) + 6.25 x height (cm) – 5 x age (y) + 5`

**Women**
`10 x weight (kg) + 6.25 x height (cm) – 5 x age (y) – 161`

### Katch-McArdle (Known BF%)  
`370 + (21.6 * LBM)`
where LBM is lean body mass


The resulting number is your BMR (basal metabolic rate). This number is then multiplied based on your activity level.  

The final number at the end of the setup is your TDEE (Amount of calories burned in one day)  
Eating below this number will result in weight loss, and above will be weight gain.


# Basic App Usage
#### Adding calories:  
Fill in the food name option if needed, and enter the calories in the box below and press enter.  
This will add your calories to the current day's log.  
#### Removing calories:  
Press and hold the calories entry you\'d like to remove, and it will be removed from your current log.  
#### Finish logging for the day:  
Press and hold the Kcal logo, this will log the current consumed calories for today and add it to the Track tab's graph.  

### Settings  
#### Custom TDEE  
Set your own custom TDEE based on your own calculations
#### Show time in entry  
If enabled, when logging calories, each entry will have the time in it.  
Ex `[12:04pm] Fish - 400`  
#### Night owl mode 
If enabled, when you finish logging for the day, if you finish before 4am it logs to the previous day.  
This is for people who are up late and continue to eat past midnight.
#### Sum same day  
When ending the day more than once on the same day, instead of overwriting the day, it sums them up and re-adds the entry.  

### Notable libraries used
Modified [Spruce Animation Library](https://github.com/amooose/spruce-android) (Custom fixed for Android 8+)  
[MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)  
[Android Gif](https://github.com/koral--/android-gif-drawable)
