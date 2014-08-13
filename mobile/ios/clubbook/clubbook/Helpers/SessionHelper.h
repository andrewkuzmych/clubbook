//
//  SessionHelper.h
//  Clubbook
//
//  Created by Andrew on 7/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "User.h"

@interface SessionHelper : NSObject

+(void) StoreUser:(User *)user;
+(void) DeleteUser;

@end
