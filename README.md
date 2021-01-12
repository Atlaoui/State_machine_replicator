# State_machine_replicator
protocole de réplication de machine à états

# Instaltation avec eclipse
```
wget http://downloads.sourceforge.net/project/peersim/peersim-1.0.5.zip

unzip peersim-1.0.5.zip

cp peersim-1.0.5/*.jar Path jusque/SMR/lib 

zip -r src.zip peersim-1.0.5/src

cp src.zip Path jusque/SMR/lib


```
# configuration eclipse

- Run -> Run configurations
- choisir Java Application
- new launch configuration 
- Search dans main class
- choisir Simulator
- appuier sur Arguments 
- dans Program arguments appuier sur variables
- ajouter le path ver le fichier de conf

# Model 

- n’importe quel nœud du système peut tomber en panne n’importe quand

- une panne peut être définitive ou temporaire

- un message peut être perdu ou dupliqué

- les canaux de communications ne sont pas FIFO

Nous considéron un eensemble de noeuds hhébergeant les réplicas noté R 

Chaque noeud R :
- est identifier par un indice i telque 0 <= i < |R|. || = ensemble ?

- Tous ri ∈ R a une historique Hi ordonnais , persistant  et stocker localment et est fiable

- L’indice d’une requête dans un historique est appelé itération >= 0

- si deux itérations x et y existent dans H et que x < y alors H(x) désigne une
requête qui a été délivrée avant H(y)

- Propriété de surté a respecter pour  ∀ Hi et ∀x ∈ Hi:
 
- soit H i (x) = ⊥ signifiant que le réplica r i ne connait pas encore la valeur de la
requête associée à l’itération x

- soit H i (x) = req tel que ∀H j 6 = H i ∧H j (x) 6 = ⊥ ⇒ H j (x) = req signifiant que tout
autre historique qui maintient une valeur différente de ⊥ à l’itération x implique
que la valeur de cette itération doit être égale à req.
# Paxos basic
Les nœuds externes au système sont appelés clients et ce sont ces nœuds qui
émettent les requêtes. Parmi l’ensemble des nœuds internes au système, le protocole Paxos
définit 3 rôles
- **Learner :** les noeuds qui hébergent les réplicas du service
- **Acceptor :** 
- - les noeuds qui permettent de prendre une décision sur la valeur. Ils
servent notamment à se rappeler si une valeur a déjà été décidée dans le passé ou
bien si une nouvelle valeur doit être décidée
- - Leur nombre dépend du nombre de
fautes que l’on souhaite tolérer

- - Les Acceptors doivent toujours former un quorum
pour que le protocole fonctionne.Un quorum peut être n’importe quel
ensemble d’Acceptor qui forme une majorité.
 

- **Proposer :** les nœuds qui reçoivent les requêtes des clients et qui les soumettent
à la décision des Acceptors, Ils sont responsables de récolter la réponse de ces
derniers pour valider ou rejeter la requête du client.


## Algo de base

- **Étape 0** : Un client choisit un Proposer p et lui envoie sa requête

- **Étape 1a** :
- -  Le Proposer p émet à l’ensemble des Acceptors un message Prepare
contenant un numéro de round (ballot ou numéro de séquence).

- - Ce
numéro de round doit être strictement supérieur à n’importe quel numéro de round
déjà envoyé par p.

- - Le but de ce message étant de savoir si un quorum d’Acceptors
peut être atteint pour pouvoir soumettre la requête du client.

- **Étape 1b** : À la réception d’un message Prepare sur un Acceptor a depuis un Proposer p pour un round n, il y a deux cas à considérer :

- - si n est supérieur à n’importe quel round auquel a ait participé, un message
Promise est envoyé à p. Ce message Promise permet d’indiquer à p que a
ne participera plus à un scrutin de round inférieur n. Le message Promise
contient éventuellement la précédente valeur v que a a déjà acceptée (lors d’une
précédente phase 2b) associé à son numéro de round. nv


- - sinon, a ignore le message ou éventuellement renvoi un message Reject à p lui
indiquant que son numéro de round est invalide et obsolète.

- **Étape 2a** : Lorsque p reçoit une majorité de Promise, il doit décider d’une valeur e.

- - S’ il existe des Promise contenant des valeurs déjà acceptées par son envoyeur,
alors p choisit la valeur v avec le numéro n v le plus grand.
- - sinon, p est libre de choisir la valeur et dans notre cas il choisira la requête que
le client lui a envoyée à l’étape 0.

Le Proposer p envoie alors à l’ensemble des Acceptors la valeur e qu’il a choisie
associée au numéro de round n qu’il a envoyé dans le message Prepare (désigné parfois aussi comme Commit)

- **Étape 2b** : À la réception d’un message Accept sur un Acceptor a depuis un Proposer
p pour un round n et une valeur e :

- - si n est plus grand ou égal au numéro de round du dernier Promise que a ait
envoyé, alors il accepte la valeur e. Il mémorise cette valeur (pour les éven-
tuels futurs Prepare) et diffuse à l’ensemble des Learners qu’à p un message
Accepted contenant la valeur e.

- - sinon le message est ignoré

- **Étape 3** : Lorsqu’un Learner l recoit une majorité de messages Accepted pour une
même valeur e alors l prend acte de cette décision (la valeur est définitivement
acceptée) et exécute la requête décrite par la valeur e. En fonction du protocole
applicatif qui caractérise la requête, l peut répondre directement au client.

# Model de Paxos utiliser

### Nœuds :
- tout nœud interne au système est à la fois Proposer, Acceptor
et Learner

- tout nœud n ∈ Π a le rôle de Proposer autrement dit éligible à l’élection de leader

- tout nœud n ∈ Π a le rôle d’Acceptor, donc pour avoir
une majorité est de recevoir N 2 + 1 message Promise et Accepted contenant la
même valeur pour pouvoir passer en phase 2 et 3

- tout nœud n ∈ Π a le rôle de Learner impliquant qu’il y a N réplica du service
(donc N historique) dans le système. À la validation d’une nouvelle itération x
dans son historique H, avant d’exécuter la requête et répondre au client, un learner
devra s’assurer qu’il n’y ait pas d’itération y < x tel que H(y) = ⊥. Ceci peut se
produire si le noeud est tombé en panne puis a redémarré ou bien si des messages
Accepted ont été perdus. Si c’est le cas, il faudra relancer un round Paxos (via le
leader) pour connaître les valeurs manquantes.