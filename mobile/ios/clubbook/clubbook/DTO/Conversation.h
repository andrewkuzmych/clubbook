//
//  Conversation.h
//  Clubbook
//
//  Created by Andrew on 7/8/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BaseDto.h"

@interface Conversation : BaseDto

@property (strong, nonatomic) NSString *user_from;
@property (strong, nonatomic) NSString *type;
@property (strong, nonatomic) NSString *msg;
@property (strong, nonatomic) NSDate *time;
@property (strong, nonatomic) NSString *url;
@property (strong, nonatomic) NSDictionary *location;
@property (assign, nonatomic) BOOL read;

@end
