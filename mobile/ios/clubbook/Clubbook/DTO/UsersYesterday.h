//
//  UsersYesterday.h
//  Clubbook
//
//  Created by Andrew on 10/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseDto.h"

@interface UsersYesterday : BaseDto

@property (strong, nonatomic) NSMutableArray *users;
@property (assign, nonatomic) BOOL hasAccess;

@end
