//
//  DateHelper.h
//  Clubbook
//
//  Created by Anton Semenyuk on 12/22/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DateHelper : NSObject

+ (DateHelper*)sharedSingleton;
-(BOOL) is24hFormat;

- (NSString*) get12hTime:(NSDate*) date;
- (NSString*) get24hTime:(NSDate*) date;

@end
