//
//  NewsData.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/14/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NewsData : NSObject

@property (strong, nonatomic) NSString* createdById;
@property (strong, nonatomic) NSDate* createDate;
@property (strong, nonatomic) NSString* newsDescription;
@property (strong, nonatomic) NSString* title;
@property (strong, nonatomic) NSMutableArray* photos;
@property (strong, nonatomic) NSMutableDictionary* tempDownlaodedPhotos;

@end
