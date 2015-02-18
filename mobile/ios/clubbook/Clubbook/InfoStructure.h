//
//  InfoStructure.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/18/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "WorkingHour.h"

#import <Foundation/Foundation.h>

@interface InfoStructure : NSObject

@property (strong, nonatomic) NSString* infoType;

@property (strong, nonatomic) NSString* infoDescription;
@property (strong, nonatomic) NSString* ageRestriction;
@property (strong, nonatomic) NSString* adress;
@property (strong, nonatomic) NSString* dressCode;
@property (strong, nonatomic) NSString* phone;
@property (strong, nonatomic) NSString* webSite;
@property (strong, nonatomic) NSString* email;
@property (strong, nonatomic) NSString* music;
@property (strong, nonatomic) NSString* name;
@property (strong, nonatomic) NSMutableArray *workingHours;
@property (strong, nonatomic) WorkingHour *todayWorkingHour;

@property (strong, nonatomic) NSMutableArray *cellsToHide;

@property NSString* lat;
@property NSString* lon;
@property int  capacity;
@property int distance;

@end
