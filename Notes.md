NOTES
===

- CascadeType.PERSIST: When persisting an entity, also persist the entities held in this field. We suggest liberal application of this cascade rule, because if the EntityManager finds a field that references a new entity during flush, and the field does not use CascadeType.PERSIST, it is an error.
- CascadeType.REMOVE: When deleting an entity, also delete the entities held in this field.
- CascadeType.REFRESH: When refreshing an entity, also refresh the entities held in this field.
- CascadeType.MERGE: When merging entity state, also merge the entities held in this field.

http://en.wikibooks.org/wiki/Java_Persistence/ManyToMany

- Patterns for entity removal (@PreRemove): http://blog.xebia.com/2009/04/09/jpa-implementation-patterns-removing-entities/

BUGS
===

- When route/group/stationmanager is deleted, ACL Entry for event "guide 'READ'" is not deleted! It can be deleted ONLY if
 the user is not guide/stationmanager at any other route

EMAILS
===

- opendayapp@gmail.com, h=&estecA!uY+da2uGa2ephu*!8pejUx
- opendayapptest@gmail.com, h=&estecA!uY+da2uGa2ephu*!8pejUx
- opendays.guide1@gmail.com
- opendays.guide2@email.cz
- opendays.guide3@email.cz
- opendays.guide4@seznam.cz

