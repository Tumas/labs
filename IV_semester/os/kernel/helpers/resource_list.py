#!/usr/bin/env python

class ResourceList:
    ''' Simple abstraction for simpler interface to resources list '''
    def __init__(self):
        self.list = {}

    def is_created(self, resouce):
        ''' checks whether given resource is created '''
        return True if resource.rid in self.list else False

    def is_instance_created(self, resource_class):
        ''' Returns true if object of given class is inside collection '''
        for resource in self.list.values():
            if resource.__class__ == resource_class:
                return True
        return False

    def get_by_class(self, class_object):
        ''' return resource if it is an instance of given class.

            note that parameter is class_object and not a name of class
            and only first element found is returned
        '''
        for resource in self.list.values():
            if resource.__class__ == class_object:
                return resource
        return None

    def count_instances(self, class_object):
        ''' return number of instance of given class that have been created '''
        count = 0
        for resource in self.list.values():
            if resource.__class__ == class_object:
                count += 1
        return count

    def add(self, resource):
        ''' adds given resource to the resource list '''
        self.list[resource.rid] = resource

    def remove(self, resource):
        ''' removes resource from resource list '''
        if resource.rid in self.list:
            del self.list[resource.rid]

    def resources(self):
        ''' returns array of resources '''
        return self.list.values()
