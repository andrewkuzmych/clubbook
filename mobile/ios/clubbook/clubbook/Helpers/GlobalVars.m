//
//  GlobalVars.m
//  Clubbook
//
//  Created by Andrew on 7/1/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "GlobalVars.h"

@implementation GlobalVars
@synthesize MaxCheckinRadius;
@synthesize MaxFailedCheckin;
@synthesize CheckinUpdateTime;

static GlobalVars *instance = nil;

+(GlobalVars *)getInstance
{
    @synchronized(self)
    {
        if(instance==nil)
        {
            instance= [GlobalVars new];
        }
    }
    return instance;
}
@end
