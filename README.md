# chatbotpoc
poc project for chatbot rules and logic

This PoC attempts to validate some of the basic rules required for executing the staples WiMS chat flows.

Little to no time spend on actual Channel or Message interfaces.  Most of work was around testing rules impl.

Quickstart:  Run ConversationClient

Design/PoC goals:
- demonstrate simple state machine which can accommodate a real world conversation flow
- create a configurable rules mechanism
- rules checks should be configurable via file (json) or java
- rule actions also should also be configurable via json
- should be able to delegate to actual java classes when things get complicated


============================
Entry Point / Scope:
============================

The core main class is:  ConversationClient
- run this class and talk to the bot.
- current rules demonstrate a basic conversation flow of three states:
- Greeting:
    figure out if its a valid intent
    transfer to agent or continue if "good" intent
- CaptureOrder:
    pull 10 digit order number out of customer message
- VerifyOrder:
    ask customer if order is correct
    move back to CaptureOrder if order number is not correct.

==============================
Rules implementation:
==============================
The implementation of the Rules involve three main Classes:

Rule
Check
Action

Rule:
- A rule has a list of "checks"
- each "check" is a simple boolean condition which will check true or false
- currently all "checks" must be true for the rule to evaluate to true
- this could be changed for different types of rules however.
- A Rule has 2 action lists
- "True actions" are executed when the rule tests true
- "False Actions" are executed when the rule tests false
- These lists are optional.  You can configure one list or both.

Check:
- As mentioned a "check" is a simple boolean check.
- There are currently 2 implemented checks:
    IntentCheck: tests true if the current intent matches and confidence is above a threshold
    can test 1 or more intents and confidences

    ScriptCheck:  uses embedded JavaScript nashorn engine.  Any javascript can be written.
    Currently tests against values in the ConversationContext (more on this below)

Action:
- Once Checks are tested on a rule a list of actions are executed.
- There are currently a few implemented actions
    SayAction:  returns text to the Channel.  "says" a message to user

    TransferAction:  transfers the customer to an agent.

    ChangeStateAction:  changes the state of the machine from A to B.

    A rule can also be an action.  So you can nest rule execution.

    Custom actions are also possible in java.  more on this below in configraiton.  An example of this is implemented
    to capture the order number using RE.

==================================
Configuration:
==================================
- Rules and actions are configured via a simple Json syntax.  for example this is a rule:

    {
      "name": "Test Rule 2","scope": "Intent",
      "check": [
        {"intents":["ShipmentTracking","Greeting"],"confidences":[0.85,0.85]}
        {"script":"context.tryCount<1"}
      ],
      "true": [
        {"say":"it was true"},
        {"state":"VerifyOrder"},
        {"transfer":"This is the transfer message"},
        {"class":"com.staples.chatbot.poc.TestAction"}
      ],
      "false": [
        {"say":"it was false"},
        {"script":"context.tryCount++"}
      ]
    }

- The above rule configuration has 2 checks.  It checks that the intent is ShipmenTracking or Greeting at the specified confidence
- It also checks that the "tryCount" is less than one.
- if both conditions are met, the actions listed under "true" are executed
- if the rule tests false the actions under "false" are executed.

- The True configuration demonstrates all 4 possible action configs:
- "say": "it was true" will pass the corresponding message back to the channel.  This should be modifiied to message keys rather than text.
- "transfer": "this is a message" will say the message and then transfer on the channel to an agent.
- "state": "NewState" updates the state of the machine. Rules are organized into states, so this basically changes the ruleset
- "class": specifies any java class that implenets the "Action" interface
- similar configuration of "class" is possible for any Check.  the class must implement the "Check" internface



==================================
State Classes:
==================================
- There are three main classes which store the state
- given demo of Apache state machine these are good caniddates to move to that model
- Conversation:  has a list of states
- ConversationState:  a "part" of a conversation.  has specific set of rules
- ConversationContext:  a map of dynamic parameters.  there is a context at the Conversation (global) and the ConversationState level
- Keeps track of code items like:
- total utterances
- understood utterances
- entry count on each state
- ....

- ConversationState is what currently holds the rules. A naming convention around conversation rules and config json is imposed.
State: StateName, then json file:  StateNameRules.json must exist

- Rules also fall into 1 of three "scopes"
- Scope = "Entry"
    entry rules are executed when you first enter into the conversation state
    no utternace from the user is required.
    these rules are executed each time you enter the state
- Scope="Intent"
    these rules are executed every time an utterance is relieved from the user.
    Watson is used to find intent, then each intent rule is executed
- Scope="Default"
    this concept could be removed.  currently when an utterance is recieved, if none of the intent rules match, then the defaul rules are executed
    this could also be accomplised with the rules config itself


This Poc code represents 2-3 hours on 2 weekend days.  It was meant to prove out basic flow and concepts required.

