//
//  FriendsResult.h
//  Clubbook
//
//  Created by Andrew on 10/12/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseDto.h"

@interface FriendsResult : BaseDto

@property (strong, nonatomic) NSMutableArray *friends;
@property (assign, nonatomic) int countOfFriends;
@property (assign, nonatomic) int countOfPendings;

@end
