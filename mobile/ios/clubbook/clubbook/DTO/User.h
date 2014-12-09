//
//  User.h
//  Clubbook
//
//  Created by Andrew on 6/19/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BaseDto.h"
#import "Place.h"

@interface User : BaseDto

@property (strong, nonatomic) NSString *id;
@property (strong, nonatomic) NSString *name;
@property (strong, nonatomic) NSString *email;
@property (strong, nonatomic) NSString *gender;
@property (strong, nonatomic) NSString *avatar;
@property (strong, nonatomic) NSString *friend_status;
@property (strong, nonatomic) NSDate *dob;
@property (strong, nonatomic) NSString *age;
@property (strong, nonatomic) NSString *bio;
@property (strong, nonatomic) NSString *country;
@property (strong, nonatomic) NSString *access_token;
@property (strong, nonatomic) NSString *currentCheckinClubName;
@property (strong, nonatomic) Place *place;
@property (strong, nonatomic) NSMutableArray *photos;
@property (assign, nonatomic) BOOL isFb;
@property (assign, nonatomic) BOOL isFriend;
@property (assign, nonatomic) BOOL push;
@property (assign, nonatomic) BOOL isVisibleNearby;
@property (assign, nonatomic) BOOL isBlocked;

@end
