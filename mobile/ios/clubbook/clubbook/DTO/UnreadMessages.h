//
//  UnreadMessages.h
//  Clubbook
//
//  Created by Andrew on 8/2/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseDto.h"

@interface UnreadMessages : BaseDto

@property (assign, nonatomic) int countOfUnreadChats;
@property (assign, nonatomic) int countOfPendingFriends;

@end
