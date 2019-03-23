## Part A - Fix current bugs

### Bug 1 - Layout does not look as expected

Edited layout **_constraintTop_toBottomOf** property of **password** field and **first_name** field so they are below the previous text boxes, ensuring the consistency.

Center logo with 
**app:layout_constraintLeft_toLeftOf="parent"**
**app:layout_constraintRight_toRightOf="parent"**

Fix sign-in logo and image by putting in a linear layout so they are closely together, and moved up with **marginTop**.


### Bug 2 - Validation is incorrect
Changed starting value of **isValid** to true, and then evaluated each condition of the loop. If one of the conditions was found to be invalid, **isValid** would be set to false. 
This fixed the problem of **isValid** being true if one of the boxes was valid, when we need 2 of the boxes at the very least to be true.
Changed if condition for **first_name** field. This only gives a validation error if anything has been entered in the field, and it was an invalid string.
Also, the error messages are reset each time the **allFieldsValid** function is called. This ensures that if a previously incorrect field of the text boxes has now been entered correctly, the error message will disappear.


### Bug 3 - Animation is looping incorrectly

Firstly, I changed the **setupAnimation** method so it only played the frames **0** to **109**. Next, I added an **animatorListener** to the animation that has an overrided **onAnimationEnd** method. This means, when the animation from **0** to **109** ends, this event is triggered which calls the loop animation method. This method sets the animation frames from **131** to **158**, and also changes the properties so this part of the animation loops indefinitely.

## Part B - Add 2 new screens

The remainder of my solution is implemented in java as this is my preferred language.
Firstly, once the **allFieldsValid** function passes, An instance of **LoginAPI** is called. This is an **AsyncTask** class that submits the email and password to the moneyBox API. If the details are incorrect, a toast will pop up asking the user to try again. If it was successful, then the API stores the bearer token (and name if entered) into the application's shared preferences before starting an intent for **AccountsActivity**.

This activity starts by using the **InvestorProductsAPI** async task with the bearer token to obtain a list of all of the user's products before displaying it in the boxes. The user's name is also displayed if it was entered. Onclick listeners are attatched to each of the boxes, so clicking on one of the boxes creates an intent for **IndividualAccountActivity**. If this API fails to get the information (e.g. if there is no internet connection or the user's bearer token has expired) then the activity will call **finish()** and navigate back to the login screen.

The **IndividualAccountActivity** activity is passed the previous data through **Intent.putExtra()**, and displays all of this additional information. The activity also has a button which when clicked, uses the **PaymentAPI** async task. This calls the final http method to add £10 to the user's particular moneybox total. If successful, then the user's price will update  on the screen as well as in the database. A toast will also pop up to say the payment was successful. If the payment fails, a toast will also pop up saying this.

