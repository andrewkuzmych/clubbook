//
//  PlaceDto.h
//  Clubbook
//
//  Created by Andrew on 6/23/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseDto.h"
#import "WorkingHour.h"

@interface Place : BaseDto

@property (strong, nonatomic) NSString *id;
@property (strong, nonatomic) NSString *title;
@property (strong, nonatomic) NSString *address;
@property (strong, nonatomic) NSString *phone;
@property (strong, nonatomic) NSString *site;
@property (strong, nonatomic) NSString *email;
@property (strong, nonatomic) NSString *avatar;
@property (strong, nonatomic) NSString *info;
@property (strong, nonatomic) NSString *lat;
@property (strong, nonatomic) NSString *lon;
@property (strong, nonatomic) NSMutableArray *photos;
@property (strong, nonatomic) NSMutableArray *users;
@property (strong, nonatomic) WorkingHour *todayWorkingHours;
@property (strong, nonatomic) NSMutableArray *workingHours;
@property (assign, nonatomic) int countOfUsers;
@property (assign, nonatomic) int friendsCount;
@property (assign, nonatomic) int capacity;
@property (strong, nonatomic) NSString *ageRestriction;
@property (strong, nonatomic) NSString *dressCode;
@property (assign, nonatomic) double disatance;

@end
