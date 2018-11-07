# P2PNode

Node 1 startes uden reference til anden node:  
TCP server socket startes på port  
Der lyttes efter beskeder

Node 1 modtager __CONNECT__ fra Node 2:  
Node 2 sætter front til Node 1's port  
Da Node 1 ikke er forbundet med andre nodes sætter den både front og back til at være Node 2's port.  
Node 1 sender dernæst en __CONNECT__ til Node 2.  
Node 2 sætter sin back til at være Node 1's port.  

Node 2 modtager __CONNECT__ fra Node 3:  
Node 3 sætter front til Node 2's port  
Da node 2 allerede har en back, sender den en __SWITCH__ til sin back, med en (Node 2 port, Node 3 port) tuple. Derefter sætter den sin back til Node 3's port.  
Node 2's back sætter  så sin front til Node 3's port i stedet, og sender en __CONNECT__ til Node 3.  
Da Node 3 ikke har en back, sætter den sin back til at være afsenderens port  


Beskeder:  

__CONNECT__:  
Indhold: intet. 
Hvis modtageren hverken har front eller start, sættes begge til afsenderens port, og en __CONNECT__ sendes tilbage afsenderen.
Hvis modtageren har en front, men ikke en back, sættes back til afsenderens port
Hvis modtageren både har en front og en back, sender modtageren en __SWITCH__ til sin egen back, hvorefter den sætter sin back til afsenderens port. 

__SWITCH__:  
Indhold: fromPort|toPort (separeres med pipe)  
Hvis modtageren's front matcher fromPort, sætter modtageren sin front til toPort, og sender en __CONNECT__ til toPort.
Hvis modtageren's front ikke matcher fromPort, sender modtageren beskeden til sin back.
