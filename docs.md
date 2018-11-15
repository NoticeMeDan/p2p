# P2PNode

Node 1 startes uden reference til anden node:  
TCP server socket startes på port  
Der lyttes efter beskeder

Node 1 modtager __CONNECT__ fra Node 2:  
Node 2 sætter switchNode til Node 1's port  
Da Node 1 ikke er forbundet med andre nodes sætter den både switchNode og node til at være Node 2's port.  
Node 1 sender dernæst en __CONNECT__ til Node 2.  
Node 2 sætter sin node til at være Node 1's port.  

Node 2 modtager __CONNECT__ fra Node 3:  
Node 3 sætter switchNode til Node 2's port  
Da node 2 allerede har en node, sender den en __SWITCH__ til sin node, med en (Node 2 port, Node 3 port) tuple. Derefter sætter den sin node til Node 3's port.  
Node 2's node sætter  så sin switchNode til Node 3's port i stedet, og sender en __CONNECT__ til Node 3.  
Da Node 3 ikke har en node, sætter den sin node til at være afsenderens port  


Beskeder:  

__CONNECT__:  
Indhold: intet. 
Hvis modtageren hverken har switchNode eller node, sættes begge til afsenderens port, og en __CONNECT__ sendes tilbage afsenderen.
Hvis modtageren har en switchNode, men ikke en node, sættes node til afsenderens port
Hvis modtageren både har en switchNode og en node, sender modtageren en __SWITCH__ til sin egen node, hvorefter den sætter sin node til afsenderens port. 

__SWITCH__:  
Indhold: fromIp|fromPort|toIp|toPort (separeres med pipe)  
Hvis modtageren's switchNode matcher fromIp/fromPort, sætter modtageren sin switchNode til toIp/toPort, og sender en __CONNECT__ til toIp/toPort.
Hvis modtageren's switchNode ikke matcher fromIp/fromPort, sender modtageren beskeden til sin node.
