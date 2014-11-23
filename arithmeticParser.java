/**
 @author: William Robert Howerton III
 @date: Nov. 20, 2014
 @function:	The purpose of this program is to determine whether or not an expression is a valid arithmatic expression. The program will print "true" if it is a valid expression or "false" if the expressoin is invalid.
 @input: a mathematical expression. including digits 0-9, letters a-Z, and the operators (,),*,/,-,+
 @output: true if the expression is valid, false if the expression is invalid
 */

/**Grammar used in implementation:
 * S->E
 * E->TE1
 * E1-> +TE1 |-TE1 | [null]
 * T-> FT1
 * T1-> *FT1 | /FT1 | [null]
 * F-> n | (E) | v
 */
import java.util.*;
import java.util.Queue;
import java.lang.Character;

/**
 --isValid			        :boolean value to keep track of whether or not the expression is valid or not.
 --Queue				    :<Character> Queue. A queue of Character objects in the form of a linked list.
 --isSign			        :boolean value to keep track of whether or not the last value seen was a sign (operator) or not
 --isDecimalAllowed			 :boolean to keep track of whether a decimal has been seen or not. initial value set to true.
 --isVarNum			        :boolean to keep track of whether or not the last character read was a letter or number
 --isOperator			    :boolean to keep track of whether or not the last character was an operator

 --func ruleE			    :Represents the E rule in grammar. Calling the E method calls the T() and E1() method
 --func ruleT			    :Represents the T rule in grammar. Calling the T method calls the F() and T1() method
 --func ruleF			    :Represents the F rule in grammar. Calling the F attempts to match the top of the Queue to a left parenthesis '('
                            if it does find a leftmost parenthesis, it calls method E and then proceeds to look for a rightmost parenthesis ')'
                            however, if there are no parenthesis, ruleF calls the isLetterOrNumber() method.
 --func ruleT1			    :Represents the T1 rule in grammar. Calling the T1 method first calls the preT1() method and if that method returns true,
                            F() and T1() methods are called again.
 --func ruleE1			    :Represents the E1 rule in grammar. Calling the E1 method first calls the preE1() method and if that method returns true,
                             T() and E1() methods are called again.
 --func preT1			     :boolean return: represents the terminal checked at the beginning of the expression. T1 is only allowed to continue if
                             T1() returns true
 --func preE1			    :boolean return: represents the terminal checked at the beginning of the expression. T1 is only allowed to continue if
                             T1() returns true
 --func isLetterOrNumber    :boolean return function to decide whether or not the next popped character is a valid letter or number variable
 --func reset()				:void return which resets parser to original starting state after each iteration when dealing with multiple strings

 */


public class ArithmeticParser{

    static Queue<Character> pQueue = new LinkedList<Character>();
    static boolean isValid = true;
    static boolean isSign = false;
    static boolean isDecimalAllowed = true;
    static boolean isVarNum = false;
    static boolean isOperator = true;


    /**main method used for expression testing. represents start variable
     @param String array called args
     @return void, but prints test results*/
    public static void main(String[] args) {


        for (int i = 0; i < args.length; i ++) {
            for (Character c : args[i].toCharArray()) pQueue.add(c);

            /**Executes "rule E" i.e. the entire parser; beginning with the first rule.
             * if a correct expression was passed through the main method, the queue should be empty after execution*/
            ruleE();

            System.out.print(i+1 + " ");
            if (pQueue.size() == 0) System.out.println(isValid);
            else System.out.println("False");
            reset(); //for multiple strings, to clean the queue after each iteration
        }

    }

    /**represents rule: E->TE1
     @param void
     @return void*/
    public static void ruleE(){
        ruleT();
        ruleE1();
    }

    /**Represents rule: E1-> +TE1 |-TE1 | [null]
     * Since E1 produces terminals followed by variables, I check for these terminals at the start by calling the method
     * preE1(). if these conditions are not satisfied, the preE1() method handles the error
     @param: void
     @return: void*/
    public static void ruleE1(){
        if (pQueue.size() > 0 && preE1()){
            pQueue.remove(); //removes element from the queue if it was verified by the preceding conditional
            ruleT(); //rule T call
            ruleE1(); //rule E1 call
        }

    }
    /**Represents rule: T-> FT1
     @param void
     @return void
     */
    public static void ruleT(){
        ruleF();
        ruleT1();
    }
    /**Represents rule T1-> *FT1 | /FT1 | [null]
     * Since T1 produces terminals followed by variables, I check for these terminals at the start of the T1 method by
     * calling the method preT1(). Like the preE1() method, if these conditions are not met, the preT1() method will
     * handle these exceptions
     @param void
     @return void */
    public static void ruleT1(){
        if ((pQueue.size() > 0) && preT1()){
            pQueue.remove();//removes element from the queue if it was verified by the preceding conditional
            ruleF();
            ruleT1();
        }
    }
    /**Represents rule: F-> n | (E) | v
     * F can produce either a variable v, or a digit n or a left parenthesis (followed by the E rule again)
     @param: void
     @return: void*/
    public static void ruleF(){

        if (pQueue.isEmpty()) isValid = false; //rule F is not allowed to terminate. if it is empty, expression is invalid
        if ((pQueue.size() > 0) && (pQueue.peek()) == '(') {
            /*checks the queue for the first element, checks whether or not the topmost character is a leftmost parenthesis*/
            pQueue.remove(); //removes element from the queue if it was verified by the preceding conditional

            ruleE(); //calls E() if the previous element was a left parenthesis

            if (pQueue.isEmpty()) isValid = false;
                 //rule F is not allowed to terminate, thus the expression is invalid
                //if the queue is empty

            else if (!pQueue.isEmpty() && pQueue.peek() != ')') {
                //checking for right parenthesis after rule E() has been called
                dump(); //dumps queue and marks invalid if no right parenthesis is found
                isValid = false;
            }
            else {
                /*if the last element on the stack was a right parenthesis, expression marked valid and element is removed*/
                pQueue.remove();//removes element from the queue if it was verified by the preceding conditional
                isValid = true;
            }
        } //end left parenthesis checker

        /*if the top element on the stack is a right parenthesis instead of a left parenthesis, the expression is
        * marked invalid and the queue is dumped*/
        else if (!pQueue.isEmpty() && pQueue.peek() == ')'){
            dump();
            isValid = false;
        } //end right parenthesis checker

        /*if the element is neither left parenthesis or a right parenthesis, the element must be a number or variable
        * the following method is called to check the validity of such claim*/
        else if(isLetterOrNumber()){}
    }

    /**Method to check whether a valid variable is passed or not. Returns true if yes, otherwise returns false
     Will return true for multiple variable sizes or multiple integer sizes
     @param void
     @return boolean; true if returned, false otherwise*/
    public static boolean isLetterOrNumber(){

        if (pQueue.size() > 0){
        /*This first if statement checks whether or not the element is a letter variable or not. I do this by utilizing
        * the java Character object class and using the .isLetter() method. if the element is any letter between lower
         * case a or uppercase Z, the method will return true*/
            if (Character.isLetter(pQueue.peek())){ //calling isLetter() method
                isVarNum = true; //sets isVarNum to true. please see documentation at the top of this file for more info
                isValid = true; //isValid set to true (letter is a valid terminal
                isSign = false; //is sign set to false. for more info, please see top of file
                pQueue.remove(); //removes element from the queue if it was verified by the preceding conditional

                /*if the last element seen was a letter, the following if statement will check whether or not the next
                * character is a decimal. if it is, the expression is deemed invalid and the queue is dumped. otherwise,
                * the following while statement will check if the next element is a letter. while the next element is a
                * character is a letter, the queue will remove these elements. if the next character seen is a digit or
                * decimal the loop will exit*/
                if ((!pQueue.isEmpty()) && (pQueue.peek() == '.')){
                    isValid = false;
                    dump();
                    return false;
                }
                while (!pQueue.isEmpty() && (Character.isLetter(pQueue.peek()))){
                    pQueue.remove();
                    if (!pQueue.isEmpty() && pQueue.peek() == '.'){
                        isValid = false;
                        dump();
                        return false;
                    }
                    else if (!pQueue.isEmpty() && Character.isDigit(pQueue.peek())) isLetterOrNumber();/*if a number is seen, the loop exits and the
                    isLetterOrNumber() method is called again to take care of proceeding numbers. this ensures that letters
                    are not allowed to proceed numbers*/
                }

                return true;
            }


            else if (Character.isDigit(pQueue.peek())||(pQueue.peek() == '.')){
                /*This next statement checks whether or not the element is a number or not. As with the variable checker,
                * I check whether or not the element is a number or not by using the isDigit() boolean method provided
                * by the Character java class. the method will return true if the element is any digit ranging from
                * 0 to 9*/
                isVarNum = true;
                isValid = true; //the same conditionals are applied as if the parser saw a letter
                isSign = false;

                /**Records whether a decimal has been seen inside the number.
                 * if there are more elements left in the queue AND the top element is a decimal && isDecimalAllowed is true (i.e. we've already seen a decimal) then
                 * we set isDecimalAllowed to false*/

                if ((pQueue.size() > 0) && (pQueue.peek() == '.' && isDecimalAllowed)) isDecimalAllowed = false; /*checks whether or
                not the first element is a decimal if the expression begins with a decimal, isDecimalAllowed set to false*/

                pQueue.remove(); //removes element from the queue if it was verified by the preceding conditional

                if (pQueue.isEmpty() && !isDecimalAllowed){
                    isValid = false;        //if the previous element was a decimal and the stack is now empty,the
                    return false;       //expression is marked invalid so it cannot end on just a decimal.
                }
                else if (pQueue.size() > 0){
                    /*makes sure the queue is not empty before proceeding. the proceeding while loop operates on the
                    * following conditions: the queue still has elements in it and the top element is a 0. if a 0 is
                    * seen, the isDecimalAllowed value is set to false. if a decimal is seen again, the condition no longer
                    * applies and the loop will exit. if only 1 or no decimals are seen, the loop iterates until all
                    * numbers are removed from the queue*/
                    while ((pQueue.size() > 0 && Character.isDigit(pQueue.peek())) || (pQueue.peek() == '.' && isDecimalAllowed)){
                        if (pQueue.peek() == '.') isDecimalAllowed = false;
                        pQueue.remove();
                        if (pQueue.isEmpty()) break; //if the queue is empty proceeding the remove() method, loop will exit
                    }
                }
                /*The following conditional checks for letters but differs from the number checker by not allowing decimals
                 * to precede letters. also, if isDecimalAllowed is still false from a previous sighting, the conditional will
                  * not allow another decimal to be accepted*/
                if (pQueue.size() > 0 && (Character.isLetter(pQueue.peek()) || (pQueue.peek() == '.' && !isDecimalAllowed))){
                    /**if another letter is seen after a number or a decimal is seen but not allowed, isValid set to false
                     * and the queue is dumped*/
                    isValid = false;
                    dump();
                }
                isDecimalAllowed = true; //isDecimalAllowed value reset to true so more numbers with decimals may follow
                return true;
            }
            else{ //isValid set to false if value is not a letter or number
                isValid = false;
            }
        }
        return false;
    }
    /*preE1() is the method called before calling ruleE(). this rule makes sure the conditions are right. some terminals
    * are not allowed: signs such as *,/,-, or + */
    public static boolean preE1(){
    /*if a sign was seen previously the expression is marked invalid and the queue is dumped*/
        if (pQueue.size() > 0){
            if (isSign){
                isValid = false;
                dump();
                return false;
            }
            /* if a positive or negative operator is allowed or a variable/number was seen last, the expression is
            *  confirmed valid*/
            else
            if(isOperator){
                if((pQueue.peek() == '+' || pQueue.peek() == '-') && isVarNum){
                    isSign = true;
                    isVarNum = false;
                    return true;
                }
            }
            else{
                dump();
                isOperator = false;
            }
        }
        return false;
    }

    /*preT1() checks whether or not the next element in the stack is a * or / before calling ruleT1()*/
    public static boolean preT1(){

        if (pQueue.size() > 0)
            if (isSign){
                //if previous character was a sign, expression dumped and marked invalid
                isValid = false;
                dump();
                return false;
            }

            else if(((pQueue.peek() == '*') || (pQueue.peek() == '/')) && isVarNum){
                isSign = true; //else if last element was variable/number and top element is a * or /, marked valid
                return true;
            }
        return false;
    }

    /*dump method: empties remainder of the queue if the expression is decided to be invalid
    * @param void
    * @return void*/
    public static void dump(){
        while (pQueue.size() > 0) pQueue.remove();
    }

    /*reset() method is purely for my own testing usage. used to "clean up" after each iteration
    * @param void
    * @return void*/

    public static void reset(){
        isValid = true;
        isSign = false;
        isDecimalAllowed = true;
        isVarNum = false;
        isOperator = true;
        dump();
    }
}
