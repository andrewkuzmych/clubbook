//
//  Chat.h
//  Clubbook
//
//  Created by Andrew on 7/8/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "User.h"

@interface Chat : NSObject

@property (strong, nonatomic) NSString *chatId;
@property (strong, nonatomic) NSMutableArray *conversations;
@property (strong, nonatomic) User *currentUser;
@property (strong, nonatomic) User *receiver;
@property (assign, nonatomic) NSInteger unreadMessages;
@end
